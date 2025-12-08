package org.example.tamaapi.command.item;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.tamaapi.domain.item.*;
import org.example.tamaapi.common.exception.NotEnoughStockException;
import org.example.tamaapi.dto.feign.requestDto.ItemOrderCountRequest;
import org.example.tamaapi.query.item.ColorItemQueryRepository;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final ColorItemQueryRepository colorItemQueryRepository;

    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;

    public List<Long> saveItem(Item item, List<ColorItem> colorItems, List<ColorItemSizeStock> colorItemSizeStocks) {
        itemRepository.save(item);
        //colorItems 객체는 bulk insert해서 PK 없는 상태
        saveColorItems(colorItems);

        //colorItem PK를 외래키로 쓰는 colorItemSizeStock을 저장하려면 PK 필요
        //colorItemSizeStock은 colorItem의 주소 값을 갖는 상태
        //즉, colorItem PK를 채우면 colorItemSizeStock 외래키도 채워짐
        List<Long> colorIds = colorItems.stream().map(c -> c.getColor().getId()).toList();

        //방금 bulk insert한 colorItem PK 조회
        List<ColorItem> foundColorItems = colorItemQueryRepository.findAllByItemIdAndColorIdIn(item.getId(), colorIds);

        //KEY:ColorId, VALUE:colorItemId
        Map<Long, Long> map = foundColorItems.stream()
                .collect(Collectors.toMap(
                        ci -> ci.getColor().getId(),
                        ColorItem::getId
                ));

        //colorItem PK 채우기
        for (ColorItem colorItem : colorItems) {
            Long savedColorItemId = map.get(colorItem.getColor().getId());
            colorItem.setIdAfterBatch(savedColorItemId);
        }

        saveColorItemSizeStocks(colorItemSizeStocks);
        return colorItems.stream().map(ColorItem::getId).toList();
    }

    //------------jdbcTemplate 로직---------------
    public void saveItems(List<Item> items) {

        String sql = """
            INSERT INTO item (
                original_price, now_price, gender, year_season, name, description,
                date_of_manufacture, country_of_manufacture, manufacturer, category_id,
                textile, precaution, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Item item = items.get(i);

                ps.setInt(1, item.getOriginalPrice());
                ps.setInt(2, item.getNowPrice());
                ps.setString(3, item.getGender().name());
                ps.setString(4, item.getYearSeason());
                ps.setString(5, item.getName());
                ps.setString(6, item.getDescription());
                ps.setDate(7, java.sql.Date.valueOf(item.getDateOfManufacture()));
                ps.setString(8, item.getCountryOfManufacture());
                ps.setString(9, item.getManufacturer());
                ps.setObject(10, item.getCategory() != null ? item.getCategory().getId() : null);
                ps.setString(11, item.getTextile());
                ps.setString(12, item.getPrecaution());
                ps.setObject(13, item.getCreatedAt()); // 외부에서 전달받은 값
                ps.setObject(14, item.getCreatedAt()); // 외부에서 전달받은 값
            }

            @Override
            public int getBatchSize() {
                return items.size();
            }
        });
    }

    public void saveColorItems(List<ColorItem> colorItems) {

        jdbcTemplate.batchUpdate("INSERT INTO color_Item(item_id, color_id) values (?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, colorItems.get(i).getItem().getId());
                ps.setLong(2, colorItems.get(i).getColor().getId());
            }
            @Override
            public int getBatchSize() {
                return colorItems.size();
            }
        });
    }

    public void saveColorItemSizeStocks(List<ColorItemSizeStock> colorItemSizeStocks) {
        jdbcTemplate.batchUpdate("INSERT INTO color_item_size_stock(color_item_id, size, stock) values (?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, colorItemSizeStocks.get(i).getColorItem().getId());
                ps.setString(2, colorItemSizeStocks.get(i).getSize());
                ps.setInt(3, colorItemSizeStocks.get(i).getStock());
            }
            @Override
            public int getBatchSize() {
                return colorItemSizeStocks.size();
            }
        });
    }

    public void saveColorItemImages(List<ColorItemImage> colorItemImages) {

        jdbcTemplate.batchUpdate("INSERT INTO color_item_image(color_item_id, original_file_name, stored_file_name, sequence) values (?, ?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, colorItemImages.get(i).getColorItem().getId());
                ps.setString(2, colorItemImages.get(i).getUploadFile().getOriginalFileName());
                ps.setString(3, colorItemImages.get(i).getUploadFile().getStoredFileName());
                ps.setInt(4, colorItemImages.get(i).getSequence());
            }
            @Override
            public int getBatchSize() {
                return colorItemImages.size();
            }
        });
    }

    public void saveReviews(List<Review> reviews) {
        String sql = "INSERT INTO review (order_item_id, member_id, rating, comment, height, weight, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Review review = reviews.get(i);

                ps.setLong(1, review.getOrderItemId());   // null 아님
                ps.setLong(2, review.getMemberId());      // null 아님
                ps.setInt(3, review.getRating());               // 기본값 존재
                ps.setString(4, review.getComment());           // null 허용 시 setObject 사용 가능
                ps.setObject(5, review.getHeight(), Types.INTEGER);
                ps.setObject(6, review.getWeight(), Types.INTEGER);
                ps.setTimestamp(7, Timestamp.valueOf(review.getCreatedAt())); // BaseEntity에서 상속
                ps.setTimestamp(8, Timestamp.valueOf(review.getUpdatedAt()));
            }

            @Override
            public int getBatchSize() {
                return reviews.size();
            }
        });
    }

    //------------fegin로직-----------------
    public void decreaseStock(Long colorItemSizeStockId, int quantity){
        //동시에 요청 오면, UPDATE 전에 재고 조회하는 게 의미가 없음
        //단일 요청이면 의미 있긴한데, 밑에 update 쿼리만으로 재고 부족 예외 throw 가능
        //그래서 if(db.stock - quantity < 0) throw 로직 제거

        //변경 감지는 갱실 분실 문제 발생 -> 직접 update로 배타적 락으로 예방
        int updated = em.createQuery("update ColorItemSizeStock c set c.stock = c.stock-:quantity " +
                        "where c.id = :id and c.stock >= :quantity")
                .setParameter("quantity", quantity)
                .setParameter("id", colorItemSizeStockId)
                .executeUpdate();

        //재고보다 주문양이 많으면 업데이트 된 row 없는 걸 이용
        if (updated == 0)
            throw new NotEnoughStockException();
    }


    public void decreaseStocks(List<ItemOrderCountRequest> requests){
        for (ItemOrderCountRequest request : requests) {
            decreaseStock(request.getColorItemSizeStockId(), request.getOrderCount());
        }
    }


    public void increaseStock(Long colorItemSizeStockId, int quantity){
        //동시에 요청 오면, UPDATE 전에 재고 조회하는 게 의미가 없음
        //단일 요청이면 의미 있긴한데, 밑에 update 쿼리만으로 재고 부족 예외 throw 가능
        //그래서 if(db.stock - quantity < 0) throw 로직 제거

        //변경 감지는 갱실 분실 문제 발생 -> 직접 update로 배타적 락으로 예방
        int updated = em.createQuery("update ColorItemSizeStock c set c.stock = c.stock + :quantity " +
                        "where c.id = :id and c.stock >= :quantity")
                .setParameter("quantity", quantity)
                .setParameter("id", colorItemSizeStockId)
                .executeUpdate();

    }

    public void increaseStocks(List<ItemOrderCountRequest> requests){
        for (ItemOrderCountRequest request : requests) {
            increaseStock(request.getColorItemSizeStockId(), request.getOrderCount());
        }
    }


}
