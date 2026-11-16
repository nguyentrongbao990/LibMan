-- Chỉ áp dụng vào database LibMan hiện có; không chạy lại schema/seed.
-- ALTER sẽ thất bại và transaction bị hủy nếu có categoryName NULL.
-- Không tự điền, sửa hoặc xóa dữ liệu để vượt qua kiểm tra.
BEGIN;
SET LOCAL lock_timeout = '5s';
LOCK TABLE "public"."tblCategory" IN ACCESS EXCLUSIVE MODE;
SELECT count(*) AS "nullCategoryNameCount"
FROM "public"."tblCategory" WHERE "categoryName" IS NULL;
ALTER TABLE "public"."tblCategory" ALTER COLUMN "categoryName" SET NOT NULL;
COMMIT;
