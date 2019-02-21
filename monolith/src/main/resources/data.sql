delete from product_tbl;
delete from inventory_item_tbl;

insert into product_tbl
  (id, name, description, code, price_in_inr)
values
  (1, 'The Alchemist', 'Paulo Coelho''s masterpiece tells the magical story of Santiago, an Andalusian shepherd boy who yearns to travel in search of a worldly treasure as extravagant as any ever found.', '9780061122415', 386),
  (2, '1984', 'Nineteen Eighty-Four, often published as 1984, is a dystopian novel by English writer George Orwell published in June 1949. The novel is set in the year 1984 when most of the world population have become victims of perpetual war, omnipresent government surveillance and propaganda.', '9788129116116', 281);

insert into inventory_item_tbl
  (id, version, product_name, product_code, quantity)
values
  (1, 1, 'The Alchemist', '9780061122415', 1),
  (2, 1, '1984', '9788129116116', 10);
