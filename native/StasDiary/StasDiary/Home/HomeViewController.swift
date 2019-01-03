//
//  HomeViewController.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 26/12/2018.
//  Copyright © 2018 Krzysztof Pobiarżyn. All rights reserved.
//
import UIKit
import sharedcode

class HomeViewController: UITableViewController {
    
    private var viewModel: MealsViewModel? = nil
    private var _presenter: MainPresenter? = nil
    private var presenter: MainPresenter {
        return _presenter!
    }
    private let selectionViewModel: AddSelectionViewModel = AddSelectionViewModel()

    private var mealTypePicker: UIView? = nil

    override func viewDidLoad() {
        super.viewDidLoad()
        setupTableView()
        self.title = "Stasiowy Dzienniczek"
        self.clearsSelectionOnViewWillAppear = false
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(
            title: "Dodaj",
            style: .plain,
            target: self,
            action: #selector(onAddTap)
        )
        self.navigationItem.leftBarButtonItem = editButtonItem
        
        _presenter = Assembly.Presenters.main(
            withController: self.navigationController!,
            popupDisplayer: self
        )
        presenter.onShow(view: self)
        
        print(MockDataProvider().provideIngredients())
    }
    
    @objc func onAddTap() {
        mealTypePicker = UIView(frame: CGRect(x: 0, y: view.frame.height - 260, width: view.frame.width, height: 260))
    
        // Toolbar
        let btnDone = UIBarButtonItem(title: "Ok", style: .done, target: self, action: #selector(donePicker))
        let spaceButton = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
        let cancelButton = UIBarButtonItem(title: "Anuluj", style: .plain, target: self, action: #selector(cancelPicker))
        
        let barAccessory = UIToolbar(frame: CGRect(x: 0, y: 0, width: mealTypePicker!.frame.width, height: 44))
        barAccessory.barStyle = .default
        barAccessory.isTranslucent = false
        barAccessory.items = [cancelButton, spaceButton, btnDone]
        mealTypePicker!.addSubview(barAccessory)
        
        let picker = UIPickerView(frame: CGRect(x: 0, y: barAccessory.frame.height, width: view.frame.width, height: mealTypePicker!.frame.height-barAccessory.frame.height))
        picker.delegate = self
        picker.dataSource = self
        picker.backgroundColor = UIColor.white
        mealTypePicker!.addSubview(picker)

        self.view.addSubview(mealTypePicker!)
        
        selectionViewModel.selectOption(position: 0)
    }
    
    @objc func donePicker() {
        dismissPicker()
        switch selectionViewModel.selectedOption.type {
        case MealType.dessert:
            presenter.onDessertClick()
        case MealType.dinner:
            presenter.onDinnerClick()
        case MealType.milk:
            presenter.onMilkClick()
        default:
            presenter.onAddIngredientClick()
        }
    }
    
    @objc func cancelPicker() {
        selectionViewModel.cancelSelection()
        dismissPicker()
    }
    
    private func setupTableView() {
        tableView.register(UINib(nibName: "MealCell", bundle: nil), forCellReuseIdentifier: "MealCell")
        tableView.register(UINib(nibName: "DayCell", bundle: nil), forCellReuseIdentifier: "DayCell")
        tableView.tableFooterView = UIView()
    }
    
    private func dismissPicker() {
        mealTypePicker?.removeFromSuperview()
        mealTypePicker = nil
    }

    // MARK: - Table view data source
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return Int(viewModel?.viewsCount ?? 0)
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let position = Int32(indexPath.row)
        
        guard let viewType = viewModel?.viewTypeByPosition(position: position) else {
            return UITableViewCell()
        }
        
        switch viewType {
        case MealsViewModel.Companion.init().label_VIEW_TYPE:
            let cell = tableView.dequeueReusableCell(withIdentifier: "DayCell", for: indexPath) as! DayCell
            cell.viewModel = viewModel?.labelViewModelByPosition(position: position)
            return cell
        case MealsViewModel.Companion.init().meal_ITEM_VIEW_TYPE:
            let cell = tableView.dequeueReusableCell(withIdentifier: "MealCell", for: indexPath) as! MealCell
            cell.viewModel = viewModel?.mealViewModelByPosition(position: position)
            return cell
        default:
            return UITableViewCell()
        }
    }
    
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        let position = Int32(indexPath.row)
        guard let viewType = viewModel?.viewTypeByPosition(position: position) else {
            return true
        }
        return viewType == MealsViewModel.Companion.init().meal_ITEM_VIEW_TYPE
    }
    
    override func tableView(_ tableView: UITableView, shouldIndentWhileEditingRowAt indexPath: IndexPath) -> Bool {
        return false
    }
    
    override func tableView(_ tableView: UITableView, editActionsForRowAt indexPath: IndexPath) -> [UITableViewRowAction]? {
        let editButton = UITableViewRowAction(style: .normal, title: "Edycja") { (rowAction, indexPath) in
            guard let mealVM = self.viewModel?.mealViewModelByPosition(position: Int32(indexPath.row)) else {
                return
            }
            self.presenter.onEditClick(item: mealVM.meal)
        }
        editButton.backgroundColor = .blue
        let deleteButton = UITableViewRowAction(style: .normal, title: "Usuń") { (rowAction, indexPath) in
            guard let mealVM = self.viewModel?.mealViewModelByPosition(position: Int32(indexPath.row)) else {
                return
            }
            self.presenter.onDeleteClick(item: mealVM.meal)
        }
        deleteButton.backgroundColor = .red
        return [deleteButton, editButton]
    }
    
}

extension HomeViewController: UIPickerViewDataSource {
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return selectionViewModel.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return selectionViewModel.names[row]
    }
}

extension HomeViewController: UIPickerViewDelegate {
 
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        selectionViewModel.selectOption(position: row)
    }
}

extension HomeViewController: PopupDisplayer {
    
    func display(viewModel: PopupViewModel) {
        
    }
}

extension HomeViewController: MainView {

    func hideMeals() { }
    
    func showMeals(viewModel: MealsViewModel) {
        self.viewModel = viewModel
        reloadInputViews()
    }

    func hideSyncBar() {
        print("hide sync bar")
    }

    func showSyncBar() {
        print("show sync bar")
    }

    func closeDrawers() {
        print("close drawers")
    }
}
