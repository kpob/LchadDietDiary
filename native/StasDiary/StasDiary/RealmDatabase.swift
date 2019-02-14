//
//  RealmDatabase.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 29/12/2018.
//  Copyright © 2018 Krzysztof Pobiarżyn. All rights reserved.
//

import Foundation
import main


class RealmDatabase: NSObject, Database {
    
    func query(parts: KotlinArray) -> [Any] {
        var qp = [QueryPart]()
        for i in 0..<parts.size {
            qp.append(parts.get(index: i) as! QueryPart)
        }
        _ = buildQuery(parts: qp)
        return [] //MockDataProvider.init().provideMeals()
    }
    
    func querySingle(parts: KotlinArray) -> Any? {
        return nil
    }
    
    
    private func buildQuery(parts: [QueryPart]) -> Int {
        var result = 0
        parts.forEach {
            switch $0 {
            case is EqualsString:
                result = 1
            case is InInts:
                result = 2
            case is InStrings:
                result = 3
            case is InLongs:
                result = 4
            case is BetweenLongs:
                result = 5
            default:
                result = 6
            }
        }
        print("result \(result)")
        return result
    }
    
}
