//
//  AddOption.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 29/12/2018.
//  Copyright © 2018 Krzysztof Pobiarżyn. All rights reserved.
//

import Foundation
import main

struct AddOption {
    
    let type: MealType?
    let displayName: String
    
    init(displayName: String) {
        self.displayName = displayName
        self.type = nil
    }
    
    init(type: MealType) {
        self.type = type
        self.displayName = type.string
    }
    
    
}
