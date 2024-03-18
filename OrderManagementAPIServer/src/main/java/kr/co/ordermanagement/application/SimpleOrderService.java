package kr.co.ordermanagement.application;

import kr.co.ordermanagement.domain.order.Order;
import kr.co.ordermanagement.domain.order.OrderRepository;
import kr.co.ordermanagement.domain.order.OrderedProduct;
import kr.co.ordermanagement.domain.order.State;
import kr.co.ordermanagement.domain.product.Product;
import kr.co.ordermanagement.domain.product.ProductRepository;
import kr.co.ordermanagement.presentation.dto.ChangeStateRequestDto;
import kr.co.ordermanagement.presentation.dto.OrderProductRequestDto;
import kr.co.ordermanagement.presentation.dto.OrderProductResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleOrderService {

    private ProductRepository productRepository;
    private OrderRepository orderRepository;

    @Autowired
    public SimpleOrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public OrderProductResponseDto createOrder(List<OrderProductRequestDto> orderProductRequestDtos) {
        List<OrderedProduct> orderedProducts = makeOrderedProducts(orderProductRequestDtos);
        DecreaseProductsAmount(orderedProducts);

        Order order = new Order(orderedProducts);
        orderRepository.add(order);

        OrderProductResponseDto orderProductResponseDto = OrderProductResponseDto.toDto(order);
        return orderProductResponseDto;
    }

    public List<OrderedProduct> makeOrderedProducts(List<OrderProductRequestDto> orderProductRequestDtos) {
        return orderProductRequestDtos
                .stream()
                .map(orderProductRequestDto -> {
                    Long productId = orderProductRequestDto.getId();
                    Product product = productRepository.findById(productId);

                    Integer orderedAmount = orderProductRequestDto.getAmount();
                    product.checkEnoughAmount(orderedAmount);

                    return new OrderedProduct(
                            productId,
                            product.getName(),
                            product.getPrice(),
                            orderProductRequestDto.getAmount()
                    );
                }).toList();
    }

    public void DecreaseProductsAmount(List<OrderedProduct> orderedProducts) {
        orderedProducts
                .stream()
                .forEach(orderedProduct -> {
                    Long productId = orderedProduct.getId();
                    Product product = productRepository.findById(productId);

                    Integer orderedAmount = orderedProduct.getAmount();
                    product.decreaseAmount(orderedAmount);

//                    productRepository.update(product);
                });
    }

    public OrderProductResponseDto findById(Long orderId) {
        Order order = orderRepository.findById(orderId);
        OrderProductResponseDto orderProductResponseDto = OrderProductResponseDto.toDto(order);
        return orderProductResponseDto;
    }

    public OrderProductResponseDto changeState(Long orderId, ChangeStateRequestDto changeStateRequestDto) {
        Order order = orderRepository.findById(orderId);
        State state = changeStateRequestDto.getState();

        order.changeStateForce(state);

        OrderProductResponseDto orderProductResponseDto = OrderProductResponseDto.toDto(order);
        return orderProductResponseDto;
    }

    public List<OrderProductResponseDto> findByState(State state) {
        List<Order> orders = orderRepository.findByState(state);

        List<OrderProductResponseDto> orderProductResponseDtos = orders
                .stream()
                .map(order -> OrderProductResponseDto.toDto(order))
                .toList();

        return orderProductResponseDtos;
    }

    public OrderProductResponseDto cancleOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId);
        order.cancel();

        OrderProductResponseDto orderProductResponseDto = OrderProductResponseDto.toDto(order);
        return orderProductResponseDto;
    }
}
