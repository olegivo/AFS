//
//  HomeView.swift
//  AFS
//
//  Created by Олег Иващенко on 26.02.2021.
//

import SwiftUI

struct HomeView: View {
    var body: some View {
        NavigationView {
            VStack {
                Spacer()
                NavigationLink (
                    destination: SettingsView(),
                    label: {
                        AFSNavigationLink(title: "Settings")
                    }
                )
                
                AFSButton(title: "Favorites") {
                    print("TODO: Favorites")
                }
                .disabled(/*@START_MENU_TOKEN@*/true/*@END_MENU_TOKEN@*/)

                AFSButton(title: "Schedule") {
                    print("TODO: Schedule")
                }
                .disabled(/*@START_MENU_TOKEN@*/true/*@END_MENU_TOKEN@*/)
            }
            .padding()
            .navigationTitle("AFS")
        }
    }
}

struct HomeView_Previews: PreviewProvider {
    static var previews: some View {
        HomeView()
    }
}

struct AFSButton: View {
    let title: String
    let action:  () -> Void
    
    init(title: String, action: @escaping () -> Void) {
        self.title = title
        self.action = action
    }
    
    var body: some View {
        Button(action: action) {
            AFSNavigationLink(title: title)
        }
    }
}

func AFSNavigationLink(title: String) -> some View {
    return
        Text(title)
        .padding(EdgeInsets(top: 8, leading: 16, bottom: 8, trailing: 16))
        .frame(maxWidth: .infinity)
        .background(Color.white)
        .overlay(
            RoundedRectangle(cornerRadius: 8)
                .stroke(lineWidth: 2)
                .foregroundColor(.blue)
        )
        .shadow(color: Color.gray.opacity(0.4), radius: 3, x: 1, y: 2)
    
}
