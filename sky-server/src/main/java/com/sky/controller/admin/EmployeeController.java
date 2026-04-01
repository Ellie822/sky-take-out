package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
/**
         前端传账号密码 → EmployeeLoginDTO
        去数据库查用户
        校验账号密码
        生成 JWT token
        把结果打包成 EmployeeLoginVO
        返回给前端 Result.success(VO)
 **/

/**
 * 员工管理
 */
@RestController
//把这个类变成控制器,标记这是一个 REST 风格的控制器,里面所有方法返回的数据，直接当成 JSON 响应给前端，不会跳转到页面
/**
 * 控制器 = 后端的 “前台接待员”
 * 接收前端传过来的数据（账号密码、参数等）
 * 把数据交给 service 去处理
 * 返回结果给前端（成功 / 失败、JSON 数据）
 */
// 只要是写接口给前端调用，就用 @RestController
@RequestMapping("/admin/employee")
//并且统一给里面所有接口加一层访问前缀
@Slf4j
@Api(tags="员工相关接口 ")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    //    @PostMapping ("/login") = 登录接口的入口前端一访问这个地址，就会进到你这个 login 方法里。
    @ApiOperation("登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        //@RequestBody 就是把前端传过来的 JSON，自动转成 Java 对象（EmployeeLoginDTO）
                /*SpringBoot 是这样匹配的：
                JSON 里的 "username"
                → 找到类里名字一样的 username 字段
                JSON 里的 "password"
                → 找到类里名字一样的 password 字段
                名字完全对应，就自动赋值进去！*/
        //返回结果统一为Result，接收参数为EmployeeLoginDTO
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);
        //能否成功登录？

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        //键值对，键为EMP_ID，值为执行employeeService.login(employeeLoginDTO)时在数据库中通过username查出来的的id，被MyBatis自动赋值的

        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),// 第1个参数：秘钥
                jwtProperties.getAdminTtl(),      // 第2个参数：过期时间
                claims);                          // 第3个参数：要存的数据（员工ID）

        //EmployeeLoginVO = 专门给前端的登录成功结果包
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("登出")
    public Result<String> logout() {
        return Result.success();
    }

    @PostMapping
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}",employeeDTO);
        System.out.println("当前线程的id："+Thread.currentThread().getId());
        //Contronller,拦截器，Servicer在一次请求里占用的是同一个线程，使用的是同一块线程空间ThreadLocal
        /**
         * 新增员工实现方法
         */
        employeeService.save(employeeDTO);
        return Result.success();
    }
}
