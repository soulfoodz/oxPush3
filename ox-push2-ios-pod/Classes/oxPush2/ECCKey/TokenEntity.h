//
//  TokenEntity.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TokenEntity : NSObject <NSCoding>{

    @public
    NSString* ID;
    NSString* application;
    NSString* issuer;
    NSString* privateKey;
    NSString* publicKey;
    NSString* keyHandle;
    NSString* userName;
    NSString* pairingTime;
    NSString* authenticationMode;
    NSString* authenticationType;
    NSString* count;
    NSString* keyName;

}

-(id)initWithID:(NSString*)ID privateKey:(NSData*)privateKey publicKey:(NSData*)publicKey;

@end
