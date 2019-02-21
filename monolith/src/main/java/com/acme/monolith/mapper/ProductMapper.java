package com.acme.monolith.mapper;

import com.acme.monolith.domain.Product;
import com.acme.monolith.resource.dto.ProductDetail;
import com.acme.monolith.resource.dto.ProductSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface ProductMapper {
  ProductSummaryResponse map(Product product);
  ProductDetail toProductDetailPartial(Product product);
}
