//
//  DiaryAppNavigator.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 29/12/2018.
//  Copyright © 2018 Krzysztof Pobiarżyn. All rights reserved.
//

import Foundation
import sharedcode
import UIKit

class DiaryAppNavigator: NSObject, AppNavigator {
    
    
    let contoller: UINavigationController
    
    init(navController: UINavigationController) {
        self.contoller = navController
    }
    
    func goToAddMealView(type: MealType) {
        self.contoller.pushViewController(AddMealViewController(), animated: true)
        print("go to add meal!!! \(type)")
    }
    
    func goToEditMealView(type: MealType, meal: Meal) {
        
    }
    
    func goToAddIngredientView() {
        
    }
    
    func goToPieChartView(ids: [String]) {
        
    }
    
    func goToPieChartView(id: String) {
        
    }
    
    func goBack() {
        _ = contoller.popViewController(animated: true)
    }
    
    
}
