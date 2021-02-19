//
//  AFSApp.swift
//  AFS
//
//  Created by Олег Иващенко on 19.02.2021.
//

import SwiftUI

@main
struct AFSApp: App {
    let persistenceController = PersistenceController.shared

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environment(\.managedObjectContext, persistenceController.container.viewContext)
        }
    }
}
