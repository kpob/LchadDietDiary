//
//  TmpViewController.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 26/12/2018.
//  Copyright © 2018 Krzysztof Pobiarżyn. All rights reserved.
//

import UIKit
import sharedcode

class TmpViewController: UIViewController {

    @IBOutlet weak var titleLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        titleLabel.text = UtilitiesKt.platformName()
    }


    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
