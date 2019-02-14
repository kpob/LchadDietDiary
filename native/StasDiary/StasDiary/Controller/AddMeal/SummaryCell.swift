//
//  SummaryCell.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 04/01/2019.
//  Copyright © 2019 Krzysztof Pobiarżyn. All rights reserved.
//

import UIKit

class SummaryCell: UITableViewCell {
    
    @IBOutlet weak var timeField: UITextField! {
        didSet {
            setTime(Date())
            setupPicker()
        }
    }
    @IBOutlet weak var totalLabel: UILabel!
    @IBOutlet weak var weightLeftField: UITextField!
    
    private let datePicker = UIDatePicker()
    
    var returnValue: ((_ value: Float)->())?

    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    @IBAction func weightLeftChanged(_ sender: UITextField) {
        let w = Float(sender.text ?? "") ?? 0
        returnValue?(w)
    }
    
    static var nib:UINib {
        return UINib(nibName: identifier, bundle: nil)
    }
    
    static var identifier: String {
        return String(describing: self)
    }
    
    func setupPicker(){
        datePicker.datePickerMode = .time
        
        let toolbar = UIToolbar()
        toolbar.sizeToFit()
        let doneButton = UIBarButtonItem(title: "Gotowe", style: .plain, target: self, action: #selector(donePicker))
        let spaceButton = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
        let cancelButton = UIBarButtonItem(title: "Anuluj", style: .plain, target: self, action: #selector(cancelPicker))
        
        toolbar.setItems([doneButton,spaceButton,cancelButton], animated: false)
        
        timeField.inputAccessoryView = toolbar
        timeField.inputView = datePicker
    }
    
    @objc func donePicker() {
        setTime(datePicker.date)
        self.contentView.endEditing(true)
    }
    
    @objc func cancelPicker() {
        self.contentView.endEditing(true)
    }
    
    private func setTime(_ date: Date) {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        timeField.text = formatter.string(from: datePicker.date)
    }
}
