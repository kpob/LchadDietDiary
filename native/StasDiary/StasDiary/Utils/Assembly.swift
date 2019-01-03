//
//  Assembly.swift
//  StasDiary
//
//  Created by Krzysztof Pobiarżyn on 29/12/2018.
//  Copyright © 2018 Krzysztof Pobiarżyn. All rights reserved.
//

import Foundation
import sharedcode
import UIKit

struct Assembly {
    
    struct Dependencies {
        
        static var remoteDb: RemoteDatabase {
            return FirebaseDatabase()
        }
        
        static var stateManager: AppSyncState {
            return AppStateManager()
        }
        
        static var eventBus: DietDiaryEventBus {
            return DietDiaryEventBus()
        }
        
        static var mealsUpdateEventReceiver: MealsUpdateEventReceiver {
            return MealsUpdateEventReceiver()
        }
        
        static func navigator(withController controller: UINavigationController) -> AppNavigator {
            return DiaryAppNavigator(navController: controller)
        }
    
        
        static var mealsRepo: Repository {
            return Repository(
                mapper: FakeMealMapper.init(),
                database: RealmDatabase()
            )
        }
    }
    
    struct Presenters {
        
        static func main(withController controller: UINavigationController, popupDisplayer: PopupDisplayer) -> MainPresenter {
            return MainPresenter(
                mealsRepository: Assembly.Dependencies.mealsRepo,
                remoteDatabase: Assembly.Dependencies.remoteDb,
                appNavigator: Assembly.Dependencies.navigator(withController: controller),
                eventBus: Assembly.Dependencies.eventBus,
                mealsUpdateEventReceiver: Assembly.Dependencies.mealsUpdateEventReceiver,
                appSyncState: Assembly.Dependencies.stateManager,
                popupDisplayer: popupDisplayer
            )
        }
    }
}
