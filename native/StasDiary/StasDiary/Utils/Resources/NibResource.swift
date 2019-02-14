//
//  NibResource.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 04/01/2019.
//  Copyright © 2019 Krzysztof Pobiarżyn. All rights reserved.
//

import Foundation

struct NibResource {
    
    let name: String
    let bundle: Bundle?
    
    init(name: String) {
        self.name = name
        self.bundle = Bundle.main
    }
    
}
