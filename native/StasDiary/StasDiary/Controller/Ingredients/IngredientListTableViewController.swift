//
//  IngredientListTableViewController.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 03/01/2019.
//  Copyright © 2019 Krzysztof Pobiarżyn. All rights reserved.
//

import UIKit
import main

class IngredientListTableViewController: UITableViewController {
    
    private var _presenter: IngredientListPresenter? = nil
    private var presenter: IngredientListPresenter { return _presenter! }
    
    private var viewModel: IngredientsViewModel? = nil {
        didSet {
            reloadInputViews()
        }
    }
        
    override func viewDidLoad() {
        super.viewDidLoad()

        // Uncomment the following line to preserve selection between presentations
        self.clearsSelectionOnViewWillAppear = false
        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        self.navigationItem.rightBarButtonItem = self.editButtonItem
        setupTableView()
        _presenter = Assembly.Presenters.ingredientList(withController: self.navigationController!)
        presenter.onShow(view: self)
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        
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
        case IngredientsViewModel.Companion.init().label_VIEW_TYPE:
            let cell = tableView.dequeueReusableCell(withIdentifier: LabelTableViewCell.identifier, for: indexPath) as! LabelTableViewCell
            cell.categoryName = viewModel?.categoryName(position: position)
            return cell
        case IngredientsViewModel.Companion.init().item_VIEW_TYPE:
            let cell = tableView.dequeueReusableCell(withIdentifier: IngredientCell.identifier, for: indexPath) as! IngredientCell
            cell.viewModel = viewModel?.item(position: position)
            return cell
        default:
            return UITableViewCell()
        }
    }
    
    override func tableView(_ tableView: UITableView, shouldIndentWhileEditingRowAt indexPath: IndexPath) -> Bool {
        return true
    }
    
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        let position = Int32(indexPath.row)
        return viewModel?.isEditable(position: position) ?? false
    }
    
    override func tableView(_ tableView: UITableView, editActionsForRowAt indexPath: IndexPath) -> [UITableViewRowAction]? {
        let pos = Int32(indexPath.row)
        let editButton = UITableViewRowAction(style: .normal, title: "Edycja") { (rowAction, indexPath) in
            guard let i = self.viewModel?.item(position: pos) else { return }
            self.presenter.onEditClick(item: i)
        }
        editButton.backgroundColor = .blue
        let deleteButton = UITableViewRowAction(style: .normal, title: "Usuń") { (rowAction, indexPath) in
            guard let i = self.viewModel?.item(position: pos) else { return }
            self.presenter.onDeleteClick(item: i)
        }
        deleteButton.backgroundColor = .red
        return [deleteButton, editButton]
    }
    
    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
     */
    
    private func setupTableView() {
        tableView.register(LabelTableViewCell.nib, forCellReuseIdentifier: LabelTableViewCell.identifier)
        tableView.register(IngredientCell.nib, forCellReuseIdentifier: IngredientCell.identifier)
        tableView.tableFooterView = UIView()
    }
}


extension IngredientListTableViewController: IngredientListView {
    
    func doInitList(ingredients: IngredientsViewModel) {
        self.viewModel = ingredients
    }
    
    func updateList(ingredients: IngredientsViewModel) {
        self.viewModel = ingredients
    }
    
    func displayMessage(message: String) {
        
    }

    var viewTitle: String {
        get {
            return self.title ?? ""
        }
        set(viewTitle) {
            self.title = viewTitle
        }
    }
    
}
