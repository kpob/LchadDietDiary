//
//  NewIngredientCell.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 04/01/2019.
//  Copyright © 2019 Krzysztof Pobiarżyn. All rights reserved.
//

import UIKit
import SearchTextField
import main

class NewIngredientCell: UITableViewCell {
    
    @IBOutlet weak var weightField: UITextField!
    @IBOutlet weak var deleteButton: UIButton!
    @IBOutlet weak var nameField: SearchTextField! {
        didSet {
            nameField.theme.bgColor = .white
            nameField.maxNumberOfResults = 15
            
        }
    }
    
    var ingredients = [Ingredient]() {
        didSet {
            nameField.filterItems(ingredients.map { SearchTextFieldItem(title: $0.name) })
            nameField.itemSelectionHandler = { filteredResults, itemPosition in
                let name = filteredResults[itemPosition].title
                self.nameField.text = name
                
                guard let i = (self.ingredients.first { $0.name == name }) else {
                    return
                }
                self.filterResult?(i)
            }
        }
    }
    
    var weightValue: ((_ value: Float)->())?
    var filterResult: ((_ value: Ingredient)->())?

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        // Configure the view for the selected state
    }

    static var nib:UINib {
        return UINib(nibName: identifier, bundle: nil)
    }
    
    static var identifier: String {
        return String(describing: self)
    }
    
    @IBAction func weightInputChanged(_ sender: UITextField) {
        let w = Float(sender.text ?? "") ?? 0
        weightValue?(w)
    }
    
}
