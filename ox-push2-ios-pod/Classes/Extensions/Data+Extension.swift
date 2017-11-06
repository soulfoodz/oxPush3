//
//  Data+Extension.swift
//  Super Gluu
//
//  Created by Nazar Yavornytskyy on 3/16/17.
//  Copyright © 2017 Gluu. All rights reserved.
//

import Foundation

extension Data {
    
    init<T>(fromArray values: [T]) {
        var values = values
        self.init(buffer: UnsafeBufferPointer(start: &values, count: values.count))
    }
    
    func toArray<T>(type: T.Type) -> [T] {
        return self.withUnsafeBytes {
            [T](UnsafeBufferPointer(start: $0, count: self.count/MemoryLayout<T>.stride))
        }
    }

    func hexEncodedString() -> String {
        return map { String(format: "%02hhx", $0) }.joined()
    }
    
}
