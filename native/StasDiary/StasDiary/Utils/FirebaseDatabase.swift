//
//  FirebaseDatabase.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 29/12/2018.
//  Copyright © 2018 Krzysztof Pobiarżyn. All rights reserved.
//

import Foundation
import main

class FirebaseDatabase: NSObject, RemoteDatabase {
    func addToken(token: String) {
        
    }
    
    func saveIngredients(data: [FbIngredient]) {
        
    }
    
    func saveIngredient(item: FbIngredient, update: Bool) {
        
    }
    
    func updateUsageCounter(itemId: String, value: Int32) {
        
    }
    
    func saveMeal(meal: FbMeal, update: Bool) {
        
    }
    
    func removeMeal(item: Meal) {
        
    }
    
    func updateMealTime(mealId: String, time: Int64) {
        
    }
    
    func deleteIngredient(item: Ingredient) {
        
    }
}
