# SHOPME
- 포트폴리오용 간단 웹사이트
- 로컬 인증 회원가입 / 주문 / 상품 관리 기능이 있는 가상 쇼핑몰
- www.shopme.space (AWS 과금시 내려갈 수 있습니다...)ㅅ

### 프로젝트 구조
![shopme](https://user-images.githubusercontent.com/115934563/223373062-efce0707-bf7e-42fe-a388-a223977c349b.png)

- 프로젝트 구조는 위와 같이 진행되었습니다.
- 추가 개선 가능 사항:
  1. S3 bucket을 이용한 이미지 저장
  2. docker를 이용한 컨테이너 환경 구축
  3. 로드 밸런싱 툴 적용

### ERD
![mermaid-diagram-2023-02-26-123533](https://user-images.githubusercontent.com/115934563/223372203-18147459-c74e-425c-bae2-a67c3a423190.png)


### Git 관리 GitKraken
![image](https://user-images.githubusercontent.com/115934563/223895883-1676010d-c40f-486f-9a3d-5fa5a4f3bee3.png)


### Directory Tree
│  ShopmeBackendApplication.java 
│                                
├─config
│  │  AuditConfig.java
│  │  PageConfig.java
│  │  SecurityConfig.java
│  │  SwaggerConfig.java
│  │  WebMvcConfig.java
│  │
│  └─oAuth
│          KakaoOAuth2Response.java
│
├─controller
│  │  AdminItemController.java
│  │  AuthController.java
│  │  CartController.java
│  │  HomeController.java
│  │  ImageController.java
│  │  ItemController.java
│  │  MemberController.java
│  │  OrderController.java
│  │
│  └─handler
│          CookieHandler.java
│          RestControllerExceptionHandler.java
│
├─domain
│  │  BaseEntity.java
│  │  BaseTimeEntity.java
│  │
│  ├─cart
│  │      Cart.java
│  │      CartItem.java
│  │
│  ├─item
│  │      Item.java
│  │      ItemImage.java
│  │      ItemImageType.java
│  │      ItemSearchDateType.java
│  │      ItemStatus.java
│  │
│  ├─member
│  │      Address.java
│  │      Member.java
│  │      Role.java
│  │      RoleAdapter.java
│  │
│  ├─order
│  │      Order.java
│  │      OrderItem.java
│  │      OrderStatus.java
│  │
│  └─resource
│          FileInfo.java
│          ImagePath.java
│
├─dto
│  ├─form
│  │      ItemForm.java
│  │      MemberForm.java
│  │      SearchForm.java
│  │
│  ├─request
│  │      CartItemRequest.java
│  │      CartOrderRequest.java
│  │      ItemImageRequest.java
│  │      OrderItemDetailRequest.java
│  │      OrderItemRequest.java
│  │
│  └─response
│          CartListResponse.java
│          ItemDetailResponse.java
│          ItemImageResponse.java
│          ItemPreviewResponse.java
│          OrderHistoryResponse.java
│          OrderItemResponse.java
│
├─exception
│  │  ExceptionClass.java
│  │
│  └─rest
│          InternalServerException.java
│          OutOfStockException.java
│          ResourceExistException.java
│          ResourceNotFoundException.java
│          RestControllerException.java
│          ValidationFailedException.java
│
├─repository
│      CartItemRepository.java
│      CartRepository.java
│      ItemImageRepository.java
│      ItemRepository.java
│      ItemRepositoryCustom.java
│      ItemRepositoryCustomImpl.java
│      MemberRepository.java
│      OrderItemRepository.java
│      OrderRepository.java
│      OrderRepositoryCustom.java
│      OrderRepositoryCustomImpl.java
│
└─service
        CartService.java
        FileService.java
        ItemImageService.java
        ItemService.java
        MemberService.java
        OrderService.java
        PaginationService.java
