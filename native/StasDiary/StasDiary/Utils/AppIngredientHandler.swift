//
//  AppIngredientHandler.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 03/01/2019.
//  Copyright © 2019 Krzysztof Pobiarżyn. All rights reserved.
//

import Foundation
import main


class AppIngredientHandler: NSObject, IngredientHandler {
    
    
    
    func save(repo: Repository, ingredientDTO: FbIngredient) {
        print("save ingredient")
    }
    
    func delete(repo: Repository, ingredient: Ingredient) {
        print("delete ingredient")
    }
    
    func asFbModel(ingredient: Ingredient) -> FbIngredient {
        return FbIngredient(
            id: ingredient.id,
            name: ingredient.name,
            mtc: ingredient.mtc,
            lct: ingredient.lct,
            carbohydrates: ingredient.carbohydrates,
            protein: ingredient.protein,
            salt: ingredient.salt,
            roughage: ingredient.roughage,
            calories: ingredient.calories,
            category: ingredient.category,
            useCount: ingredient.useCount,
            deleted: false)
    }
    
}
