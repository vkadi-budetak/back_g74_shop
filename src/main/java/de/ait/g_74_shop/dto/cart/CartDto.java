package de.ait.g_74_shop.dto.cart;

import de.ait.g_74_shop.dto.position.PositionDto;

import java.util.HashSet;
import java.util.Set;

public class CartDto {

    private Set<PositionDto> positions = new HashSet<>();

    public CartDto() {
    }

    public Set<PositionDto> getPositions() {
        return positions;
    }

    public void setPositions(Set<PositionDto> positions) {
        this.positions = positions;
    }

    @Override
    public String toString() {
        return String.format("Cart DTO: positions - %d", positions.size());
    }
}
