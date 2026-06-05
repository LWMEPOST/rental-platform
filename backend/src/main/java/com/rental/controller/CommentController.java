package com.rental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.common.result.Result;
import com.rental.entity.RentalComment;
import com.rental.entity.RentalOrder;
import com.rental.service.RentalCommentService;
import com.rental.service.RentalOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private RentalCommentService commentService;
    @Autowired
    private RentalOrderService orderService;

    @GetMapping("/list/{deviceId}")
    public Result<List<RentalComment>> getList(@PathVariable Long deviceId) {
        return Result.success(commentService.getByDeviceId(deviceId));
    }

    @GetMapping("/user/list")
    public Result<List<RentalComment>> getUserComments(@RequestParam Long userId,
                                                       @RequestParam(required = false) Long orderId) {
        if (orderId != null) {
            RentalComment comment = commentService.getUserComment(userId, orderId);
            return Result.success(comment == null ? List.of() : List.of(comment));
        }
        return Result.success(commentService.getByUserId(userId));
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody RentalComment comment) {
        try {
            commentService.addComment(comment);
            return Result.success("评价成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody RentalComment comment) {
        try {
            commentService.updateComment(comment);
            return Result.success("修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/user/{id}")
    public Result<String> deleteByUser(@PathVariable Long id, @RequestParam Long userId) {
        try {
            commentService.deleteCommentByUser(id, userId);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/admin/list")
    public Result<Page<RentalComment>> adminList(@RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer size,
                                                 @RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) String orderNo) {
        Page<RentalComment> commentPage = new Page<>(page, size);
        LambdaQueryWrapper<RentalComment> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(q -> q.like(RentalComment::getUsername, keyword)
                    .or()
                    .like(RentalComment::getContent, keyword));
        }
        if (StringUtils.hasText(orderNo)) {
            List<Long> orderIds = orderService.list(new LambdaQueryWrapper<RentalOrder>()
                            .like(RentalOrder::getOrderNo, orderNo.trim()))
                    .stream()
                    .map(RentalOrder::getId)
                    .collect(Collectors.toList());
            if (orderIds.isEmpty()) {
                return Result.success(commentPage);
            }
            wrapper.in(RentalComment::getOrderId, orderIds);
        }
        wrapper.orderByDesc(RentalComment::getCreateTime);
        Page<RentalComment> result = commentService.page(commentPage, wrapper);
        commentService.fillDisplayFields(result.getRecords());
        return Result.success(result);
    }

    @DeleteMapping("/admin/{id}")
    public Result<String> deleteByAdmin(@PathVariable Long id) {
        try {
            commentService.deleteCommentByAdmin(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
