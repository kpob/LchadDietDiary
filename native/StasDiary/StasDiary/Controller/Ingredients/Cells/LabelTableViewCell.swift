//
//  LabelTableViewCell.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 03/01/2019.
//  Copyright © 2019 Krzysztof Pobiarżyn. All rights reserved.
//

import UIKit

class LabelTableViewCell: UITableViewCell {

    @IBOutlet weak var categoryLabel: UILabel!
    
    var categoryName: String? = nil {
        didSet {
            self.backgroundColor = UIColor(red: 0.0117, green: 0.1607, blue: 0.39607, alpha: 1.0)
            categoryLabel.text = categoryName ?? ""
            categoryLabel.textColor = .white
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
