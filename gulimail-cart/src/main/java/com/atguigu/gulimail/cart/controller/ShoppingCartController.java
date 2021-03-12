package com.atguigu.gulimail.cart.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimail.cart.service.ShoppingCartService;
import com.atguigu.gulimail.cart.vo.ShoppingCartVo;
import com.atguigu.gulimail.cart.vo.ShoppingItems;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 购物车处理器
 */
@Slf4j
@Controller
public class ShoppingCartController {


    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * jd 浏览器的cookie 有一个user-key 标识用户身份,一个月后过期
     * 如果第一次使用 jd 的购物车功能,都会临时生成一个user-key ,浏览器以后每次访问都会带上user-key
     * <p>
     * 登录: session有
     * 未登录: 按照 cookie带来的 user-key
     * 第一次: 如果没有临时用户,则需要帮忙创建一个临时用户
     *
     * @return
     */
    @GetMapping("/cart.html")
    public String pageList(Model model) {
        ShoppingCartVo shoppingCartVo = null;
        try {
            shoppingCartVo = shoppingCartService.getCartVo();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        model.addAttribute("shoppingCartVo", shoppingCartVo);
        return "cartList";
    }


    /**
     * 重复刷新 http://cart.gulimail.com/addToCart?skuId=27&num=2 这个请求会 重复执行添加 这个商品件数的 问题?  重复刷新
     * 解决办法 :  RedirectAttributes  的 addFlashAttribute将数据放入session中,其中数据只能使用一次,可以在页面取出只能取一次
     * 的addAttribute 在url后面自动拼接上参数 ?skuId=值
     * 添加商品到 购物车页面
     *
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes model) {
        ShoppingItems shoppingItems = null;
        try {
            shoppingItems = shoppingCartService.addToCart(skuId, num);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //携带数据到 重定向的controller
        model.addAttribute("skuId", skuId);
        log.info("addToCart:  skuId={}", skuId);
        return "redirect:http://cart.gulimail.com/addToCart.html";
    }


    @GetMapping("/addToCart.html")
    public String addToCartSuccess(@RequestParam("skuId") Long skuId,
                                   Model model) {

        ShoppingItems items = shoppingCartService.getCartItemBySkuId(skuId);
        //重定向到成功页面 再次查询购物车数据
        model.addAttribute("shoppingItems", items);
        //跳转到购物车成功页面
        return "success";
    }


    /**
     * 购物车选中/不选中单击事件
     *
     * @return
     */
    @GetMapping("/cartItem")
    public String cartItem(@RequestParam("skuId") Long skuId,
                           @RequestParam("check") Integer check) {
        shoppingCartService.checkCartItem(skuId, check);
        return "redirect:http://cart.gulimail.com/cart.html";
    }


    @GetMapping("/countItemNum")
    public String countItemNum(@RequestParam("skuId") Long skuId,
                               @RequestParam("count") Integer count) {
        shoppingCartService.countItemNum(skuId, count);
        return "redirect:http://cart.gulimail.com/cart.html";
    }


    @GetMapping("/deleteCartItem")
    public String deleteCartItem(@RequestParam("skuId") Long skuId) {
        shoppingCartService.deleteCartItemBySkuId(skuId);
        return "redirect:http://cart.gulimail.com/cart.html";
    }


    @ResponseBody
    @GetMapping("/currentUserCartItems")
    public R currentUserCartItems() {
        List<ShoppingItems> shoppingItems = shoppingCartService.getUserCartItems();
        return R.ok().setData(shoppingItems);
    }
}
