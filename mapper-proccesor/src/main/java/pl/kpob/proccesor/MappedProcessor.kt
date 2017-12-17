package pl.kpob.proccesor

import com.google.common.base.CaseFormat
import com.squareup.kotlinpoet.*
import pl.kpob.mapper_annotation.Ignore
import pl.kpob.mapper_annotation.MapAs
import pl.kpob.mapper_annotation.AutoMapping
import java.io.File
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements


class MappedProcessor : AbstractProcessor() {

    private val outputDir get() = File("${System.getProperty("user.dir")}/app/build/generated/source/kapt/debug")

    private var filer: Filer? = null
    private var messager: Messager? = null
    private var elements: Elements? = null
    private lateinit var fields: MutableList<Pair<String, String>>

    private lateinit var domainFileBuilder: FileSpec.Builder
    private lateinit var fbFileBuilder: FileSpec.Builder
    private lateinit var repoFileBuilder: FileSpec.Builder
    private lateinit var mapperFileBuilder: FileSpec.Builder

    @Synchronized override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        filer = processingEnvironment.filer
        messager = processingEnvironment.messager
        elements = processingEnvironment.elementUtils
        fields = mutableListOf()

        domainFileBuilder = FileSpec.builder(packageName + ".domain", "domain_model")
        repoFileBuilder = FileSpec.builder(packageName + ".repo", "repositories")
        fbFileBuilder = FileSpec.builder(packageName + ".firebase", "firebase_model")
        mapperFileBuilder = FileSpec.builder(packageName + ".mapper", "mappers")
        fbFileBuilder.addType(buildFirebaseInterface())
    }

    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        try {
            for (element in roundEnvironment.getElementsAnnotatedWith(AutoMapping::class.java)) {
                println(element)

                val config = element.getAnnotation(AutoMapping::class.java).let {
                    MappingConfig(it.generateFirebaseModel, it.generateRepository, it.generateDomainModel, it.generateContract)
                }
                val fields = element.enclosedElements.filter { it.kind == ElementKind.FIELD }

                val domainElements = fields
                        .filter { it.getAnnotation(Ignore::class.java) == null }
                        .map {
                            val annotation = it.getAnnotation(MapAs::class.java)
                            if (annotation != null) {
                                CustomElement(it.name, annotation.mapAs, it.typeName)
                            } else {
                                CustomElement(it.name, it.name, it.typeName)
                            }
                        }

                val inputClass = element.simpleName.toString()
                val domainClassName = element.simpleName.toString().dropLast(3)

                if(config.hasDomainModel) {
                    val domainClass = buildDomainClass(domainClassName, domainElements)
                    domainFileBuilder.addType(domainClass)
                    domainFileBuilder.build().writeTo(outputDir)
                }

                if(config.hasFirebase) {
                    buildFirebaseModel(fields, domainClassName, inputClass)
                }

                if(config.hasContract) {
                    repoFileBuilder.addType(TypeSpec.objectBuilder("${domainClassName}Contract")
                            .addProperty(PropertySpec.builder("TABLE_NAME", String::class)
                                    .initializer("%S", inputClass)
                                    .build())
                            .addProperties(
                                    fields.map {
                                        val name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, it.name).toUpperCase()
                                        PropertySpec.builder(name, String::class)
                                                .initializer("%S", it.name)
                                                .build()
                                    }
                            )
                            .build())
                }

                if(config.hasRepo) {
                    buildRepo(domainClassName, inputClass, domainElements)
                }

                repoFileBuilder.writeToFile()
            }

        } catch (ignored: IOException) { // Exception ignored : attempt to reopen a file for path
            ignored.printStackTrace()
        }

        return true
    }

    private fun buildFirebaseModel(fields: List<Element>, domainClassName: String, inputClass: String) {
        val firebaseElements = fields.map {
            CustomElement(it.name, it.name, it.typeName)
        }

        val fbClass = buildFbClass("Fb$domainClassName", inputClass, firebaseElements)
        fbFileBuilder.addAndWriteToFile(fbClass)
    }

    private fun buildRepo(domainClassName: String, inputClass: String, domainElements: List<CustomElement>) {
        mapperFileBuilder.addAndWriteToFile(buildMapperClass(domainClassName, inputClass, domainElements))
        repoFileBuilder.addType(buildRepoClass(domainClassName, inputClass))
    }

    private fun buildFirebaseInterface() =
            TypeSpec
                .interfaceBuilder(FIREBASE_INTERFACE)
                .addTypeVariable(TypeVariableName.invoke("T", ClassName("io.realm", "RealmObject")))
                .addFunction(FunSpec.builder("toRealm")
                        .returns(TypeVariableName.invoke("T"))
                        .addModifiers(KModifier.ABSTRACT)
                        .build())
                .addProperty(PropertySpec.builder("deleted", Boolean::class.java).build())
                .build()

    private fun buildFbClass(fbClassName: String, inputClassName: String, elements: List<CustomElement>): TypeSpec {
        val inputClass = ClassName("$packageName.db", inputClassName)
        val (params, properties) = elements.map {
                    ParameterSpec.builder(it.name, it.typeName).defaultValue(it.defaultValue).build() to PropertySpec.builder(it.name, it.typeName).initializer(it.name).build()
                }
                .let { it.map { it.first } to it.map { it.second } }


        return TypeSpec
                .classBuilder(fbClassName)
                .addModifiers(KModifier.DATA)
                .addSuperinterface(ParameterizedTypeName.get(FIREBASE_INTERFACE, inputClass))
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameters(params)
                        .addParameter(
                                ParameterSpec.builder("deleted", Boolean::class.java, KModifier.OVERRIDE)
                                        .defaultValue("false")
                                        .build())
                        .build())
                .apply {
                    addProperties(properties)
                    addProperty(PropertySpec
                            .builder("deleted", Boolean::class.java, KModifier.OVERRIDE)
                            .initializer("deleted")

                            .build())
                }
                .addFunction(FunSpec.builder("toRealm")
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(inputClass)
                        .addStatement(
                                                "return %L(%L)",
                                                inputClass,
                                                elements.map {
                                                    "${it.name}=${it.name}"
                                                }.reduce { acc, e -> "$acc,$e" }
                                        )
                        .build())
                .build()
    }

    private fun buildDomainClass(domainClassName: String, domainElements: List<CustomElement>) =
            TypeSpec
                    .classBuilder(domainClassName)
                    .addModifiers(KModifier.DATA)
                    .primaryConstructor(
                            FunSpec.constructorBuilder().apply {
                                val params = domainElements.map { ParameterSpec.builder(it.mapName, it.typeName).build() }
                                addParameters(params)
                            }.build())
                    .apply {
                        val properties = domainElements.map {
                            PropertySpec.builder(it.mapName, it.typeName).initializer(it.mapName).build()
                        }
                        addProperties(properties)
                    }
                    .build()

    private fun buildRepoClass(domainClassName: String, inputClassName: String): TypeSpec {
        val superclass = ParameterizedTypeName.get(
                REPO_INTERFACE,
                ClassName(PACKAGE_DB, inputClassName),
                ClassName(PACKAGE_DOMAIN, domainClassName)
        )
        return TypeSpec.classBuilder("${domainClassName}Repository")
                .superclass(superclass)
                .addSuperclassConstructorParameter("%L()", "$PACKAGE_MAPPER.${domainClassName}Mapper")
                .build()
    }

    private fun buildMapperClass(domainClassName: String, inputClassName: String, elements: List<CustomElement>): TypeSpec {
        val inputClass = ClassName(PACKAGE_DB, inputClassName)
        val outputClass = ClassName(PACKAGE_DOMAIN, domainClassName)

        val superclass = ParameterizedTypeName.get(MAPPER_INTERFACE, inputClass, outputClass)

        val inputList = ParameterizedTypeName.get(List::class.asClassName(), WildcardTypeName.subtypeOf(inputClass))
        val outputList = ParameterizedTypeName.get(List::class.asClassName(), WildcardTypeName.subtypeOf(outputClass))

        return TypeSpec.classBuilder("${domainClassName}Mapper")
                .addSuperinterface(superclass)
                .addFunction(
                        FunSpec.builder("map")
                                .addModifiers(KModifier.OVERRIDE)
                                .addParameter("input", inputClass.asNullable())
                                .returns(outputClass.asNullable())
                                .addStatement("if(input == null) return null")
                                .addStatement(
                                        "return $domainClassName(%L)",
                                        elements.map {
                                            "${it.mapName}=input.${it.name}"
                                        }.reduce { acc, e -> "$acc,$e" })
                                .build()
                )
                .addFunction(
                        FunSpec.builder("map")
                                .addModifiers(KModifier.OVERRIDE)
                                .addParameter("input", inputList)
                                .returns(outputList)
                                .addStatement("return input.mapNotNull { map(it) }")
                                .build()
                )
                .build()
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    private val Element.typeName: TypeName get() =
        if(asType().asTypeName() == ClassName("java.lang", "String")) {
            ClassName("kotlin", "String")
        } else {
            asType().asTypeName()
        }
    private val Element.name: String get() = toString()

    private fun FileSpec.Builder.addAndWriteToFile(vararg spec: TypeSpec) {
        spec.forEach { addType(it) }
        build().writeTo(outputDir)
    }

    private fun FileSpec.Builder.writeToFile() {
        build().writeTo(outputDir)
    }

    companion object {
        private val packageName = "pl.kpob.dietdiary"
        private val PACKAGE_MAPPER = "$packageName.mapper"
        private val PACKAGE_REPO = "$packageName.repo"
        private val PACKAGE_DOMAIN = "$packageName.domain"
        private val PACKAGE_DB = "$packageName.db"
        private val PACKAGE_FB = "$packageName.firebase"

        private val FIREBASE_INTERFACE = ClassName(PACKAGE_FB, "FirebaseModel")
        private val REPO_INTERFACE = ClassName(PACKAGE_REPO, "RealmRepository")
        private val MAPPER_INTERFACE = ClassName(PACKAGE_REPO, "Mapper")
    }
}
