package kr.co.ordermanagement.presentation.controller;

import kr.co.ordermanagement.application.SimpleOrderService;
import kr.co.ordermanagement.domain.order.State;
import kr.co.ordermanagement.presentation.dto.ChangeStateRequestDto;
import kr.co.ordermanagement.presentation.dto.OrderProductRequestDto;
import kr.co.ordermanagement.presentation.dto.OrderProductResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderRestController {
    private SimpleOrderService simpleOrderService;

    @Autowired
    public OrderRestController(SimpleOrderService simpleOrderService) {
        this.simpleOrderService = simpleOrderService;
    }

    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    public ResponseEntity<OrderProductResponseDto> createOrder(@RequestBody List<OrderProductRequestDto> orderProductRequestDtos) {
        OrderProductResponseDto orderProductResponseDto = simpleOrderService.createOrder(orderProductRequestDtos);
        return ResponseEntity.ok(orderProductResponseDto);
    }

    @RequestMapping(value = "/orders/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<OrderProductResponseDto> getOrderById(@PathVariable Long orderId) {
        OrderProductResponseDto orderProductResponseDto = simpleOrderService.findById(orderId);
        return ResponseEntity.ok(orderProductResponseDto);
    }

    @RequestMapping(value = "/orders/{orderId}", method = RequestMethod.PATCH)
    public ResponseEntity<OrderProductResponseDto> changeOrderState(@PathVariable Long orderId, @RequestBody ChangeStateRequestDto changeStateRequestDto) {
        if(changeStateRequestDto.getState().equals(State.CREATED) ||
                changeStateRequestDto.getState().equals(State.SHIPPING) ||
                changeStateRequestDto.getState().equals(State.COMPLETED) ||
                changeStateRequestDto.getState().equals(State.CANCELED)) {
            OrderProductResponseDto orderProductResponseDto = simpleOrderService.changeState(orderId, changeStateRequestDto);
            return ResponseEntity.ok(orderProductResponseDto);
        } else {
            throw new RuntimeException("존재하지 않는 주문상태입니다.");
        }
    }

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public ResponseEntity<List<OrderProductResponseDto>> getOrdersByState(@RequestParam State state) {
        List<OrderProductResponseDto> orderProductResponseDtos = simpleOrderService.findByState(state);
        return ResponseEntity.ok(orderProductResponseDtos);
    }

    @RequestMapping(value = "/orders/{orderId}/cancel", method = RequestMethod.PATCH)
    public ResponseEntity<OrderProductResponseDto> cancelOrderById(@PathVariable Long orderId) {
        OrderProductResponseDto orderProductResponseDto = simpleOrderService.cancleOrderById(orderId);
        return ResponseEntity.ok(orderProductResponseDto);
    }
}
