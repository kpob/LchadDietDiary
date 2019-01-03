//
//  DayCell.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 26/12/2018.
//  Copyright © 2018 Krzysztof Pobiarżyn. All rights reserved.
//

import UIKit
import sharedcode

class DayCell: UITableViewCell {

    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var lctLabel: UILabel!
    @IBOutlet weak var kcalLabel: UILabel!
    
    var viewModel: DayLabelViewModel? = nil {
        didSet {
            guard let vm = viewModel else {
                return
            }
            
            self.backgroundColor = UIColor(red: 0.0117, green: 0.1607, blue: 0.39607, alpha: 1.0)
            dateLabel.text = vm.date
            lctLabel.text = vm.lct
            kcalLabel.text = vm.calories
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
    
}
