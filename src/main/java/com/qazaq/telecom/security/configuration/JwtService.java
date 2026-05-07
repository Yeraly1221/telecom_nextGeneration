package com.qazaq.telecom.security.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import javax.xml.crypto.Data;
import java.security.Key;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/*
у нас есть header and Payload
Header
json{
  "alg": "HS256",   // алгоритм подписи
  "typ": "JWT"      // тип токена
}
Payload
{
  "sub": "alice",                    // кто это (username)
  "iat": 1700000000,                 // когда создан (issued at)
  "exp": 1700086400,                 // когда истекает (expiration)
  "roles": ["USER"]                  // роли пользователя
}
Signature
HMACSHA256(
    base64(header) + "." + base64(payload),
    secretKey   // ← твой секретный ключ на сервере
)
у нас токен поделен на 3 части 1) Header 2) Payload 3)Signature
Header  + Payload  →  просто BASE64  они перевелены в другой формат
Signaure это уже шифроание а HMACSHA256 хэш функция
 */
@Service
public class JwtService {
    private static final String SECRET_KEY = "to6OxVh41PKr5a/z+5NXKge+moMt7Fpjrgm4xpqpzb8=";

    /*public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails) {
        return Jwts
                .builder() // начинаем строить токен
                .setClaims(extraClaims) //добовляем доп данные
                .setSubject(userDetails.getUsername()) //добовляем имя пользовотеля
                .setIssuedAt(new Date(System.currentTimeMillis())) //время создания токена
                .setExpiration(new Date(System.currentTimeMillis() + 360000)) //время окончание токена
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                //подписавыет данные секретным ключом
                .compact(); // и собираем в одну
    }
    */


    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)          // ← дополнительные данные в токен
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSignInKey())
                .compact();
    }
    /*
    У тот метод унас перет секретный ключ потом делает из него масив Byte[] после
    уже эти байты преврашяются в обект Key который используется для подписования ключом и пробверки
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //<T> T можкт вернуть любой тип String , Data, любой

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        /*
        Claims — это объект внутри JWT payload:
         он  примерно выгледит как key: value
         ты даеш Claims, но возврашяеш T (тип который ты передал
         */
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        /*
        он проверяед токен на валидность то есть срок и тот ли user
         */
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpires(token);
    }

    private boolean isTokenExpires(String token) {
        return extractExpiration(token).before(new Date());
        //проверяет истек или нет срок токена
    }


    //парсер - это программа или компонент который разбирает данные и
    //превращает их в удобную структуру для работы
    //check the sign and if all things all ok give payload
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}