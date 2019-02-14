//
//  StatsViewController.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 06/01/2019.
//  Copyright © 2019 Krzysztof Pobiarżyn. All rights reserved.
//

import UIKit
import main
import Charts

class StatsViewController: DiaryVC {
    
    private var _presenter: ChartPresenter? = nil
    private var presenter: ChartPresenter { return _presenter! }
    
    private var viewModel: SummaryTabsViewModel = SummaryTabsViewModel(ingredients: [], meals: []) {
        didSet {
            pieChartUpdate()
        }
    }
  
    private let ids: [String]
    @IBOutlet weak var pieChartView: PieChartView! {
        didSet {
            pieChartView.isHidden = true
        }
    }
    @IBOutlet weak var tabs: UISegmentedControl!
    @IBOutlet weak var statsTableView: UITableView! {
        didSet {
            statsTableView.delegate = self
            statsTableView.dataSource = self
            statsTableView.register(StatsTableViewCell.nib, forCellReuseIdentifier: StatsTableViewCell.identifier)
            statsTableView.tableFooterView = UIView()
        }
    }
    
    
    init(ids: [String]) {
        self.ids = ids
        super.init(nibResource: Nibs.ViewControllers.stats)
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        _presenter = Assembly.Presenters.stats(ids: ids)
        presenter.onShow(view: self)
    }
    
    @IBAction func tabChanged(_ sender: UISegmentedControl) {
        let idx = sender.selectedSegmentIndex
        
        pieChartView.isHidden = idx == 0
        statsTableView.isHidden = idx != 0
    }
    
    func pieChartUpdate() {
        if viewModel.nutrients.count == 0 {
            return
        }
        
        let entries = viewModel.nutrients.map { (k, v) in
            return PieChartDataEntry(value: Double(truncating: v), label: k)
        }
        
        let dataSet = PieChartDataSet(values: entries, label: "Składniki odżywcze")
        let data = PieChartData(dataSet: dataSet)
        pieChartView.data = data
        pieChartView.chartDescription?.enabled = false
        
        dataSet.colors = ChartColorTemplates.pastel()
        //dataSet.valueColors = [UIColor.black]
        pieChartView.holeColor = UIColor.clear
        pieChartView.chartDescription?.textColor = UIColor.white
        
        pieChartView.legend.font = UIFont(name: "Futura", size: 14)!
        pieChartView.legend.textColor = .black
        
        // Refresh chart with new data
        pieChartView.notifyDataSetChanged()
    }
}

extension StatsViewController: ChartView {
    
    func setupView(viewModel: SummaryTabsViewModel) {
        self.viewModel = viewModel
//        print(viewModel)
        statsTableView.reloadData()
        
    }
    
    var viewTitle: String {
        get { return self.title ?? "" }
        set(viewTitle) {
            self.title = viewTitle
        }
    }
    
    
}


extension StatsViewController: UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        print("count \(viewModel.ingredients.count)")
        return viewModel.ingredients.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: StatsTableViewCell.identifier, for: indexPath) as! StatsTableViewCell
        cell.viewModel = viewModel.ingredients[indexPath.row]
        return cell
    }
    
}


extension StatsViewController: UITableViewDelegate {
    
}
