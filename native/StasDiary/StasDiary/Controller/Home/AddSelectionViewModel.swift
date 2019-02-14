//
//  HomeViewModel.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 29/12/2018.
//  Copyright © 2018 Krzysztof Pobiarżyn. All rights reserved.
//

import Foundation
import main

class AddSelectionViewModel {
    
    private static let options = [
        AddOption(type: MealType.dessert),
        AddOption(type: MealType.milk),
        AddOption(type: MealType.dinner),
        AddOption(displayName: "Posiłek")
    ]
    
    private var _selectedOption: AddOption? = nil

    var names: [String] {
        return AddSelectionViewModel.options.map { $0.displayName }
    }
    
    var count: Int {
        return AddSelectionViewModel.options.count
    }
    
    var selectedOption: AddOption {
        return _selectedOption!
    }

    func selectOption(position: Int) {
        _selectedOption = AddSelectionViewModel.options[position]
    }
    
    func cancelSelection() {
        _selectedOption = nil
    }
    
}
