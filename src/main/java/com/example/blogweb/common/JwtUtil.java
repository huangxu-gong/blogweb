package com.example.blogweb.common;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
public class JwtUtil {
//    定义一个用于 JWT（JSON Web Token）签名和验证的“对称密钥”（Secret Key）。
    private static final String SECRET ="blog-system-secret-key-please-change-me-2024-long-enough";
    private static final long EXPIRE = 7 * 24 * 60 * 60 * 1000L;

//    定义一个公开的静态方法，用于在用户登录成功后签发 Token。它接收用户的 ID 和用户名作为参数，返回一个 JWT 字符串。
    public static String createToken(Long userId, String username){
//        调用 JJWT 库的 Jwts.builder() 创建一个 JWT 构建器（Builder），准备开始拼装 Token 的数据。
        return Jwts.builder()
//        将用户名设置为 JWT 标准载荷（Payload）中的 sub（Subject）字段。sub 是 JWT 规范中预定义的字段，通常用来标识该 Token 的主体（即当前用户）。
                .setSubject(username)
//        添加一个自定义声明（Claim）。因为 JWT 标准字段里没有 userId，所以我们手动将用户的 ID 存入载荷中。这样后续请求只需携带 Token，后端就能知道是谁在操作。
                .claim("userId",userId)
//        设置 Token 的过期时间。System.currentTimeMillis() 获取当前时间的毫秒数，加上 EXPIRE（7天），生成一个未来的时间点。超过这个时间，Token 就会失效。
                .setExpiration(new Date(System.currentTimeMillis() +EXPIRE))
//        对 Token 进行签名。
//        SECRET.getBytes()：将密钥字符串转换为字节数组。
//        Keys.hmacShaKeyFor(...)：JJWT 提供的工具方法，将字节数组转换为 HMAC-SHA 算法所需的密钥对象（SecretKey）。
////        signWith(...)：使用这个密钥对前面的 Header 和 Payload 进行加密签名，防止数据被篡改。
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
//        将上述拼装好的所有信息（Header、Payload、Signature）进行 Base64 编码，并用 . 拼接，最终生成一个标准的 JWT 字符串并返回。
                .compact();
    }
//    定义一个公开的静态方法，用于验证和解析前端传来的 Token。它接收 Token 字符串，返回 Claims 对象（即解析后的载荷数据）。
    public static Claims parseToken(String token) {
//        调用 Jwts.parserBuilder() 创建一个 JWT 解析器构建器。
        return Jwts.parserBuilder()
//        配置解析器的验签密钥。必须使用与生成时相同的 SECRET，否则无法验证签名的合法性。
                .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
//        构建并生成最终的解析器（Parser）实例。
                .build()
//        执行核心的验证和解析动作。它会检查 Token 的结构、验证签名是否匹配、检查是否过期。如果验证失败（比如 Token 被篡改或已过期），这里会直接抛出异常（如 ExpiredJwtException 或 SignatureException）。
                .parseClaimsJws(token)
//        如果上面的验证全部通过，这个方法会提取出 Token 的载荷部分（即 Claims 对象），里面包含了我们存入的用户信息。
                .getBody();
    }
//    这是一个封装好的便捷方法。它内部调用了 parseToken，然后从解析出的 Claims 对象中，通过 get 方法取出之前存入的自定义字段 "userId"，并指定其类型为 Long
    public static Long getUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    /**
     * 从 token 中获取用户名
     */
//    同样是一个封装好的便捷方法。它通过 JWT 标准的 getSubject() 方法，直接获取之前存入的用户名。
    public static String getUsername(String token) {
        return parseToken(token).getSubject();
    }
}
