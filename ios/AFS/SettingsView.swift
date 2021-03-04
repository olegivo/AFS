//
//  SettingsView.swift
//  AFS
//
//  Created by Олег Иващенко on 04.03.2021.
//

import SwiftUI

struct SettingsView: View {
    var body: some View {
        VStack(alignment: .leading, spacing: nil) {
            Spacer()
            
            AFSButton(title: "Удалить БД") {
                print("TODO: Удалить БД")
            }
            .disabled(/*@START_MENU_TOKEN@*/true/*@END_MENU_TOKEN@*/)

            AFSButton(title: "Choose club") {
                print("TODO: Choose club")
            }
            .disabled(/*@START_MENU_TOKEN@*/true/*@END_MENU_TOKEN@*/)

            AFSButton(title: "Выбрать дефолтный клуб") {
                print("TODO: Выбрать дефолтный клуб")
            }
            .disabled(/*@START_MENU_TOKEN@*/true/*@END_MENU_TOKEN@*/)

            Toggle(isOn: .constant(false)) {
                Text("Fake reserve")
            }
            .disabled(true)
            .fixedSize()

        }
        .padding()
        .navigationBarTitle(Text("Settings"), displayMode: .inline)
    }
}

struct SettingsView_Previews: PreviewProvider {
    static var previews: some View {
        SettingsView()
    }
}
