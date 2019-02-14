//
//  AppTemplateCreator.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 03/01/2019.
//  Copyright © 2019 Krzysztof Pobiarżyn. All rights reserved.
//

import Foundation
import main

class AppTemplateHandler: NSObject, TemplateCreator, TemplateSaver {
    
    func save(template template_: MealTemplateDTO) {
        print("TODO save template")
    }
    
    
    func create(name: String, mealType: MealType, ingredients: [Ingredient]) -> MealTemplateDTO {
        return MealTemplateModel(
            id: UtilitiesKt.nextId(),
            name: name,
            type: mealType.string,
            ingredientIds: ingredients.map { $0.id }
        )
    }
    
    
}
