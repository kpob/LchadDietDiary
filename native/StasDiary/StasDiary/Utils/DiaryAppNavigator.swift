//
//  DiaryAppNavigator.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 29/12/2018.
//  Copyright © 2018 Krzysztof Pobiarżyn. All rights reserved.
//

import Foundation
import main
import UIKit

class DiaryAppNavigator: NSObject, AppNavigator {
    
    let contoller: UINavigationController
    
    init(navController: UINavigationController) {
        self.contoller = navController
    }
    
    func goToAddMealView(type: MealType) {
        let vc = Assembly.ViewControllers.addMeal(ofType: type)
        self.contoller.pushViewController(vc, animated: true)
        print("go to add meal!!! \(type)")
    }
    
    func goToEditMealView(type: MealType, meal: Meal) {
        
    }
    
    func goToAddIngredientView(item: FbIngredient?) {
        print("goToAddIngredientView \(item)")
    }
    
    func goToPieChartView(ids: [String]) {
        print("go to day summary")
    }
    
    func goToPieChartView(id: String) {
        goTo(Assembly.ViewControllers.singleMealStats(withId: id))
        print("go to meal summary")
    }
    
    func goBack() {
        _ = contoller.popViewController(animated: true)
    }
    
    func goToRangeSummary(first: Day, last: Day) {
        
    }
    
    func goToIngredientList() {
        self.contoller.pushViewController(IngredientListTableViewController(), animated: true)
    }
    
    private func goTo(_ vc: UIViewController) {
        self.contoller.pushViewController(vc, animated: true)
    }
}
