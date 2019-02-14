//
//  IngredientCell.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 03/01/2019.
//  Copyright © 2019 Krzysztof Pobiarżyn. All rights reserved.
//

import UIKit
import main

class IngredientCell: UITableViewCell {

    @IBOutlet weak var nameLabel: UILabel!
    
    var viewModel: Ingredient? = nil {
        didSet {
            nameLabel.text = viewModel?.name
        }
    }
    
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
    
}
