//
//  Assembly.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 29/12/2018.
//  Copyright © 2018 Krzysztof Pobiarżyn. All rights reserved.
//

import Foundation
import main
import UIKit

struct Assembly {
    
    struct Repositories {
        
        static var ingredient: Repository {
            return Repository(
                mapper: FakeIngredientMapper(),
                database: RealmDatabase()
            )
        }
        
        static func meals(byType type: MealType) -> Repository {
            let repo = Assembly.Repositories.ingredient
            let data: [Ingredient] = repo.query(spec: IngredientsByMealTypeSpecification(type: type)) as! [Ingredient]
            
            return Repository(
                mapper: MealMapper(ingredients: data),
                database: RealmDatabase()
            )
        }
        
        static var meals: Repository {
            return Repository(
                mapper: FakeMealMapper(),
                database: RealmDatabase()
            )
        }
        
        static func mealDetails(byType type: MealType) -> Repository {
            let repo = Assembly.Repositories.ingredient
            let data: [Ingredient] = repo.query(spec: IngredientsByMealTypeSpecification(type: type)) as! [Ingredient]
            
            return Repository(
                mapper: MealDetailsMapper(ingredients: data),
                database: RealmDatabase()
            )
        }
        
        static var mealDetails: Repository {
            return Repository(
                mapper: FakeMealDetailsMapper(),
                database: RealmDatabase()
            )
        }
        
        static var templates: Repository {
            return Repository(
                mapper: MealTemplateMapper(ingredients: []),
                database: RealmDatabase()
            )
        }
        
        
    }
    
    struct Dependencies {
        
        static var remoteDb: RemoteDatabase {
            return FirebaseDatabase()
        }
        
        static var stateManager: AppSyncState {
            return AppStateManager()
        }
        
        static var eventBus: DietDiaryEventBus {
            return DietDiaryEventBus()
        }
        
        static var mealsUpdateEventReceiver: MealsUpdateEventReceiver {
            return MealsUpdateEventReceiver()
        }
        
        static func navigator(withController controller: UINavigationController) -> AppNavigator {
            return DiaryAppNavigator(navController: controller)
        }
    
        
        static var ingredientHandler: IngredientHandler {
            return AppIngredientHandler()
        }
        
        static var tokenProvider: UserTokenProvider {
            return AppTokenProvider()
        }
        
        static var mealSaver: MealSaver {
            return AppMealSaver()
        }
        
        static func templateManager(type: MealType) -> TemplateManager {
            let templateHandler = AppTemplateHandler()
            return DefaultTemplateManager(
                repo: Assembly.Repositories.templates,
                creator: templateHandler,
                saver: templateHandler,
                type: type
            )
        }
    }
    
    struct Presenters {
        
        static func main(withController controller: UINavigationController, popupDisplayer: PopupDisplayer) -> MainPresenter {
            return MainPresenter(
                mealsRepository: Assembly.Repositories.meals,
                remoteDatabase: Assembly.Dependencies.remoteDb,
                appNavigator: Assembly.Dependencies.navigator(withController: controller),
                eventBus: Assembly.Dependencies.eventBus,
                mealsUpdateEventReceiver: Assembly.Dependencies.mealsUpdateEventReceiver,
                appSyncState: Assembly.Dependencies.stateManager,
                popupDisplayer: popupDisplayer
            )
        }
        static func stats(ids: [String]) -> ChartPresenter {
            return ChartPresenter(
                ids: ids,
                mealDetailsRepository: Assembly.Repositories.mealDetails
            )
        }
        
        static func ingredientList(withController controller: UINavigationController) -> IngredientListPresenter {
            return IngredientListPresenter(
                appNavigator: Assembly.Dependencies.navigator(withController: controller),
                ingredientRepository: Assembly.Repositories.ingredient,
                mealRepository: Assembly.Repositories.meals,
                remoteDatabase: Assembly.Dependencies.remoteDb,
                ingredientHandler: Assembly.Dependencies.ingredientHandler
            )
        }
        
        static func addMeal(withController controller: UINavigationController, popupDisplayer: PopupDisplayer, mealType: MealType, meal: Meal?) -> AddMealPresenter {
            return AddMealPresenter(
                mealType: mealType,
                meal: meal,
                remoteDb: Assembly.Dependencies.remoteDb,
                mealDetailsRepository: Assembly.Repositories.meals(byType: mealType),
                mealsRepository: Assembly.Repositories.mealDetails(byType: mealType),
                ingredientRepository: Assembly.Repositories.ingredient,
                templateManager: Assembly.Dependencies.templateManager(type: mealType),
                appNavigator: Assembly.Dependencies.navigator(withController: controller),
                mealSaver: Assembly.Dependencies.mealSaver,
                popupDisplayer: popupDisplayer,
                tokenProvider: Assembly.Dependencies.tokenProvider
            )
        }
    }
    
    struct ViewControllers {
        
        static func addMeal(ofType type: MealType) -> AddMealViewController {
            return AddMealViewController(mealType: type)
        }
        
        
        static func edit(meal: Meal, ofType type: MealType) -> AddMealViewController {
            return AddMealViewController(mealType: type, meal: meal)
        }
        
        static func singleMealStats(withId id: String) -> StatsViewController {
            return StatsViewController(ids: [id])
        }
        
        static func multipleMealsStats(withIds ids: [String]) -> StatsViewController {
            return StatsViewController(ids: ids)
        }
    }
}
