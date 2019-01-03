//
//  MealCell.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 26/12/2018.
//  Copyright © 2018 Krzysztof Pobiarżyn. All rights reserved.
//

import UIKit
import sharedcode

class MealCell: UITableViewCell {

    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var lctLabel: UILabel!
    @IBOutlet weak var kcalLabel: UILabel!
    @IBOutlet weak var mealIconView: UIImageView!
    
    var viewModel: MealItemViewModel? = nil {
        didSet {
            guard let vm = viewModel else {
                return
            }
            
            timeLabel.text = vm.time
            lctLabel.text = vm.lct
            kcalLabel.text = vm.calories
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    
    
}
