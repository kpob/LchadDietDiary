//
//  StatsTableViewCell.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 06/01/2019.
//  Copyright © 2019 Krzysztof Pobiarżyn. All rights reserved.
//

import UIKit
import main

class StatsTableViewCell: UITableViewCell {
    
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var caloriesLabel: UILabel!
    var viewModel: MealIngredient? = nil {
        didSet {
            guard let vm = viewModel else { return }
            
            nameLabel.text = vm.name
            caloriesLabel.text = vm.caloriesString
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
