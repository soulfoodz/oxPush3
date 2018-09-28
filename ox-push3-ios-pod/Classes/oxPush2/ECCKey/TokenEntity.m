//
//  ECCKey.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "TokenEntity.h"

@implementation TokenEntity

-(id)initWithID:(NSString*)iD privateKey:(NSString*)PrivateKey publicKey:(NSString*)PublicKey{

    _ID = iD;
    _privateKey = PrivateKey;
    _publicKey = PublicKey;
    
    return self;
}
    
/* This code has been added to support encoding and decoding my objecst */
    
-(void)encodeWithCoder:(NSCoder *)encoder
    {
        //Encode the properties of the object
        [encoder encodeObject:_ID forKey:@"ID"];
        [encoder encodeObject:_application forKey:@"application"];
        [encoder encodeObject:_issuer forKey:@"issuer"];
        [encoder encodeObject:_privateKey forKey:@"privateKey"];
        [encoder encodeObject:_publicKey forKey:@"publicKey"];
        [encoder encodeObject:_keyHandle forKey:@"keyHandle"];
        [encoder encodeObject:_userName forKey:@"userName"];
        [encoder encodeObject:_pairingTime forKey:@"pairingTime"];
        [encoder encodeObject:_authenticationMode forKey:@"authenticationMode"];
        [encoder encodeObject:_authenticationType forKey:@"authenticationType"];
        [encoder encodeObject:_count forKey:@"count"];
        [encoder encodeObject:_keyName forKey:@"keyName"];
    }
    
-(id)initWithCoder:(NSCoder *)decoder
    {
        self = [super init];
        if ( self != nil )
        {
            //decode the properties
            _ID = [decoder decodeObjectForKey:@"ID"];
            _application = [decoder decodeObjectForKey:@"application"];
            _issuer = [decoder decodeObjectForKey:@"issuer"];
            _privateKey = [decoder decodeObjectForKey:@"privateKey"];
            _publicKey = [decoder decodeObjectForKey:@"publicKey"];
            _keyHandle = [decoder decodeObjectForKey:@"keyHandle"];
            _userName = [decoder decodeObjectForKey:@"userName"];
            _pairingTime = [decoder decodeObjectForKey:@"pairingTime"];
            _authenticationMode = [decoder decodeObjectForKey:@"authenticationMode"];
            _authenticationType = [decoder decodeObjectForKey:@"authenticationType"];
            _count = [decoder decodeObjectForKey:@"count"];
            _keyName = [decoder decodeObjectForKey:@"keyName"];
        }
        return self;
    }

-(BOOL)isExternalKey{
    return [_privateKey isEqualToString:@""] || [_publicKey isEqualToString:@""];
}

@end
