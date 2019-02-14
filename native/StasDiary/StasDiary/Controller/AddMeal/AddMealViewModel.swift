//
//  AddMealViewModel.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 04/01/2019.
//  Copyright © 2019 Krzysztof Pobiarżyn. All rights reserved.
//
//
//import Foundation
//
//enum AddMealItemType {
//    case regular
//    case add
//    case summary
//}
//
//protocol AddMealItem {
//    var type: AddMealItemType { get }
//}
//
//class IngredientMealItem: AddMealItem {
//    
//    let type: AddMealItemType = .regular
//    var weight: Float = 0.0
//}
//
//class AddNextItem: AddMealItem {
//    
//    let type: AddMealItemType = .add
//    
//}
//
//
//class MealSummaryItem: AddMealItem {
//    
//    let type: AddMealItemType = .summary
//}
//
//class AddMealViewModel {
//    
//    var dataItems = [IngredientMealItem]()
//    private let summaryItem = MealSummaryItem()
//    private let nextItem = AddNextItem()
//    
//    func addRow() -> Int {
//        dataItems.append(IngredientMealItem())
//        return dataItems.count - 1
//    }
//    
//    func removeRow(at position: Int) {
//        dataItems.remove(at: position)
//    }
//    
//    func itemType(by position: Int) -> AddMealItemType {
//        return items[position].type
//    }
//    
//    func updateItem(at position: Int, with weight: Float)  {
//        dataItems[position].weight = weight
//    }
//    
//    var weight: Float {
//        
//    }
//    
//    var items: [AddMealItem] {
//        var array = [AddMealItem]()
//        array.append(contentsOf: dataItems)
//        
//        array.append(nextItem)
//        array.append(summaryItem)
//        return array
//    }
//    
//    
//    var count: Int {
//        return dataItems.count + 2
//    }
//    
//}
