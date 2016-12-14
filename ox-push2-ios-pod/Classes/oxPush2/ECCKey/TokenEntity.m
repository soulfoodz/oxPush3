//
//  ECCKey.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "TokenEntity.h"

@implementation TokenEntity

-(id)initWithID:(NSString*)ID privateKey:(NSString*)privateKey publicKey:(NSString*)publicKey{

    ID = ID;
    privateKey = privateKey;
    publicKey = publicKey;
    
    return self;
}
    
/* This code has been added to support encoding and decoding my objecst */
    
-(void)encodeWithCoder:(NSCoder *)encoder
    {
        //Encode the properties of the object
        [encoder encodeObject:ID forKey:@"ID"];
        [encoder encodeObject:application forKey:@"application"];
        [encoder encodeObject:issuer forKey:@"issuer"];
        [encoder encodeObject:privateKey forKey:@"privateKey"];
        [encoder encodeObject:publicKey forKey:@"publicKey"];
        [encoder encodeObject:keyHandle forKey:@"keyHandle"];
        [encoder encodeObject:userName forKey:@"userName"];
        [encoder encodeObject:pairingTime forKey:@"pairingTime"];
        [encoder encodeObject:authenticationMode forKey:@"authenticationMode"];
        [encoder encodeObject:authenticationType forKey:@"authenticationType"];
        [encoder encodeObject:count forKey:@"count"];
        [encoder encodeObject:keyName forKey:@"keyName"];
    }
    
-(id)initWithCoder:(NSCoder *)decoder
    {
        self = [super init];
        if ( self != nil )
        {
            //decode the properties
            ID = [decoder decodeObjectForKey:@"ID"];
            application = [decoder decodeObjectForKey:@"application"];
            issuer = [decoder decodeObjectForKey:@"issuer"];
            privateKey = [decoder decodeObjectForKey:@"privateKey"];
            publicKey = [decoder decodeObjectForKey:@"publicKey"];
            keyHandle = [decoder decodeObjectForKey:@"keyHandle"];
            userName = [decoder decodeObjectForKey:@"userName"];
            pairingTime = [decoder decodeObjectForKey:@"pairingTime"];
            authenticationMode = [decoder decodeObjectForKey:@"authenticationMode"];
            authenticationType = [decoder decodeObjectForKey:@"authenticationType"];
            count = [decoder decodeObjectForKey:@"count"];
            keyName = [decoder decodeObjectForKey:@"keyName"];
        }
        return self;
    }

@end
