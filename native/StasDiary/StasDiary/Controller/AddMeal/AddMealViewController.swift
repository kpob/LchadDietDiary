//
//  AddMealViewController.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 04/01/2019.
//  Copyright © 2019 Krzysztof Pobiarżyn. All rights reserved.
//

import UIKit
import main
import SearchTextField

class AddMealViewController: DiaryTableVC {

    private var _presenter: AddMealPresenter? = nil
    private var presenter: AddMealPresenter { return _presenter! }
    
    private var viewModel: AddMealViewModel {
        return presenter.viewModel
    }
    
    private let type: MealType
    private let meal: Meal?
    
    init(mealType: MealType, meal: Meal? = nil) {
        self.type = mealType
        self.meal = meal
        super.init(nibResource: Nibs.ViewControllers.addMeal)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(
            title: "Dodaj",
            style: .done,
            target: self,
            action: #selector(onDoneTap)
        )
        
        tableView.register(NewIngredientCell.nib, forCellReuseIdentifier: NewIngredientCell.identifier)
        tableView.register(AddNextCell.nib, forCellReuseIdentifier: AddNextCell.identifier)
        tableView.register(SummaryCell.nib, forCellReuseIdentifier: SummaryCell.identifier)
        tableView.tableFooterView = UIView()
        tableView.allowsSelection = false
        tableView.separatorStyle = .none
        
        _presenter = Assembly.Presenters.addMeal(
            withController: self.navigationController!,
            popupDisplayer: self,
            mealType: type,
            meal: meal
        )
        presenter.onShow(view: self)
    }
    
    // MARK: - Table view data source
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if _presenter == nil {
            return 0
        }
        return Int(viewModel.count)
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        let position = indexPath.row
        
        switch viewModel.itemTypeAt(position: Int32(position)) {
        case is NextButtonItem:
            return 66.0
        case is SummaryItem:
            return 40.0
        default:
            return 54.0
        }
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let position = Int32(indexPath.row)

        switch viewModel.itemTypeAt(position: position) {
        case is NextButtonItem:
            let cell = tableView.dequeueReusableCell(withIdentifier: AddNextCell.identifier, for: indexPath) as! AddNextCell
            cell.addButton.addTarget(self, action: #selector(onAddRowTap(target:)), for: .touchUpInside)
            return cell
        case is SummaryItem:
            let cell = tableView.dequeueReusableCell(withIdentifier: SummaryCell.identifier, for: indexPath) as! SummaryCell
            cell.totalLabel.text = "Razem: \(totalWeight) g"
            cell.returnValue = { weight in
                self.viewModel.updateSummary(weight: weight)
            }
            return cell
        default:
            let cell = tableView.dequeueReusableCell(withIdentifier: NewIngredientCell.identifier, for: indexPath) as! NewIngredientCell
            cell.weightField.text = ""
            cell.nameField.text = ""
            cell.nameField.hideResultsList()
            cell.ingredients = viewModel.ingredients
            cell.weightValue = { weight in
                let totalWeight = self.viewModel.updateItemAt(position: position, weight: weight)
                self.totalWeight = totalWeight
            }
            cell.filterResult = { ingredient in
                self.viewModel.updateItemAt(position: position, ingredient: ingredient)
            }
            cell.deleteButton.addTarget(indexPath, action: #selector(onDeleteTap(sender:)), for: .touchUpInside)
            return cell
        }
    }
    
    @objc func onAddRowTap(target: Any?) {
        addRow(possibleIngredients: viewModel.ingredients)
    }
    
    @objc func onDeleteTap(sender: UIButton) {
        let cell = sender.superview?.superview as! UITableViewCell
        guard let indexPath = tableView.indexPath(for: cell) else {
            return
        }
    
        tableView.beginUpdates()
        viewModel.removeRowAt(position: Int32(indexPath.row))
        tableView.deleteRows(at: [indexPath], with: .automatic)
        tableView.endUpdates()
        totalWeight = viewModel.weight
    }

    @objc func onDoneTap() {
        print(viewModel.mealParts)
        print(viewModel.left)
        let result = presenter.onAddClick(data: viewModel.mealParts, left: viewModel.left)
        print(result)
    }
}

extension AddMealViewController: PopupDisplayer {
    
    func display(viewModel: PopupViewModel) {
        print(viewModel)
    }
}

extension AddMealViewController: AddMealView {
    
    func displayError(message: String) {
        print("error msg: \(message)")
        let alert = UIAlertController(title: "Błąd", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
    
    func setExistingData(ingredients: [MealIngredient], possibleIngredients: [Ingredient]) {
        
    }
    
    func addInitialRow(possibleIngredients: [Ingredient]) {
        let _ = viewModel.addRow()
        reloadInputViews()
    }
    
    func addRow(possibleIngredients: [Ingredient]) {
        let row = viewModel.addRow()
        tableView.beginUpdates()
        tableView.insertRows(at: [IndexPath(row: Int(row), section: 0)], with: .automatic)
        tableView.endUpdates()
    }
    
    func addRows(ingredients: [Ingredient], possibleIngredients: [Ingredient]) {
        
    }
    
    var totalWeight: Float {
        get { return viewModel.weight }
        set(totalWeight) {
            let indexPath = IndexPath(row: tableView.numberOfRows(inSection: 0) - 1, section: 0)
            tableView.reloadRows(at: [indexPath], with: .none)
        }
    }
    
    var time: String {
        get { return ""}
        set(time) { }
    }
    
    var viewTitle: String {
        get { return self.title ?? "" }
        set(viewTitle) {
            self.title = viewTitle
        }
    }
}
