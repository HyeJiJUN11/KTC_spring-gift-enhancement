package gift.service;

import gift.dto.OptionDTO;
import gift.dto.PageRequestDTO;
import gift.dto.InputProductDTO;
import gift.dto.ProductDTO;
import gift.model.Category;
import gift.model.Option;
import gift.model.Product;
import gift.repository.CategoryRepository;
import gift.repository.OptionReposityory;
import gift.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OptionReposityory optionReposityory;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, OptionReposityory optionReposityory) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.optionReposityory = optionReposityory;
    }

    //전체 조회
    public Page<ProductDTO> getAllProducts(PageRequestDTO pageRequestDTO){
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage(),
                pageRequestDTO.getSize(), pageRequestDTO.getSort());
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(ProductDTO::getProductDTO);
    }

    //하나 조회
    public ProductDTO getProductDTOById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 상품이 없습니다."));
        ProductDTO productDTO = new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.getCategory().getName());
        return productDTO;
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 상품이 없습니다."));
    }

    //저장
    public void saveProduct(InputProductDTO inputProductDTO) {
        Category category = categoryRepository.findByName(inputProductDTO.getCategory())
                .orElseThrow(() -> new NoSuchElementException("해당 카테고리가 없습니다."));

        Option option = new Option(inputProductDTO.getOption());
        optionReposityory.save(option);

        Product product = new Product(
                inputProductDTO.getName(),
                inputProductDTO.getPrice(),
                inputProductDTO.getImageUrl(),
                category,
                option
                );

        productRepository.save(product);
    }

    //삭제
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void updateProduct(Long id, InputProductDTO inputProductDTO) {
        Product oldProduct = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 상품이 없습니다."));
        Category category = null;
        if (inputProductDTO.getCategory() != null) {
            category = categoryRepository.findByName(inputProductDTO.getCategory())
                    .orElseThrow(() -> new NoSuchElementException("해당 카테고리가 없습니다."));
        }
        Product updatedProduct = oldProduct.update(
                inputProductDTO.getName(),
                inputProductDTO.getPrice(),
                inputProductDTO.getImageUrl(),
                category);
        productRepository.save(updatedProduct);
    }

    public OptionDTO getOptions(Long id){
        Product product = getProductById(id);
        Option option = product.getOption();
        OptionDTO optionDTO = new OptionDTO(
                option.getId(),
                option.getOptionList()
        );
        return optionDTO;
    }

    public int getPreviousPage(Page<ProductDTO> productPage) {
        if (productPage.hasPrevious()) {
            Pageable previousPageable = productPage.previousPageable();
            return previousPageable.getPageNumber();
        }
        return -1;
    }

    public int getNextPage(Page<ProductDTO> productPage) {
        if (productPage.hasNext()) {
            Pageable nextPageable = productPage.nextPageable();
            return nextPageable.getPageNumber();
        }
        return -1;
    }
}