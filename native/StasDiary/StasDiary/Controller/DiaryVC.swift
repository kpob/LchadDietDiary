//
//  DiaryVC.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 04/01/2019.
//  Copyright © 2019 Krzysztof Pobiarżyn. All rights reserved.
//

import Foundation
import UIKit

class DiaryVC: UIViewController {
    
    init(nibResource: NibResource) {
        super.init(nibName: nibResource.name, bundle: nibResource.bundle)
    }
    
    @available(*, unavailable)
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
}