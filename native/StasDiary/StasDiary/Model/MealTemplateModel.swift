//
//  MealTemplateModel.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 03/01/2019.
//  Copyright © 2019 Krzysztof Pobiarżyn. All rights reserved.
//

import Foundation
import main


class MealTemplateModel: NSObject, MealTemplateDTO {
    
    var id: String
    var name: String
    var type: String
    var ingredientIds: [String]
    

    init(id: String, name: String, type: String, ingredientIds: [String]) {
        self.id = id
        self.name = name
        self.type = type
        self.ingredientIds = ingredientIds
    }
}
