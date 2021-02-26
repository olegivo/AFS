//
//  HomeView.swift
//  AFS
//
//  Created by Олег Иващенко on 26.02.2021.
//

import SwiftUI

struct HomeView: View {
    var body: some View {
        VStack {
            Spacer()
            AFSButton(title: "Settings") {
                print("TODO: Settings")
            }
            AFSButton(title: "Favorites") {
                print("TODO: Favorites")
            }
            AFSButton(title: "Schedule") {
                print("TODO: Schedule")
            }
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
            Text(title)
                .padding(EdgeInsets(top: 8, leading: 16, bottom: 8, trailing: 16))
                .background(Color.white)
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(lineWidth: 2)
                        .foregroundColor(.blue)
                )
                .shadow(color: Color.gray.opacity(0.4), radius: 3, x: 1, y: 2)
        }
    }
}
