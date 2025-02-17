package com.sbs.example.board.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sbs.example.board.dto.Article;
import com.sbs.example.board.dto.Comment;
import com.sbs.example.board.util.DBUtil;
import com.sbs.example.board.util.SecSql;

public class ArticleDao {
	Connection conn;

	public ArticleDao(Connection conn) {
		this.conn = conn;

	}

	public int doWrite(String title, String body, int logonMemberId) {
		SecSql sql = new SecSql();

		sql.append("INSERT INTO article");
		sql.append("SET regDate = NOW()");
		sql.append(", updateDate = NOW()");
		sql.append(", memberId = ?", logonMemberId);
		sql.append(", title = ?", title);
		sql.append(", body = ?", body);
		sql.append(", hit = 0");

		return DBUtil.insert(conn, sql);
	}

	public int getArticleCntById(int id) {
		SecSql sql = new SecSql();

		sql.append("SELECT COUNT(*)");
		sql.append("FROM article");
		sql.append("WHERE id = ?", id);
		return DBUtil.selectRowIntValue(conn, sql);
	}

	public List<Article> getArticles(int limitFrom, int limitTake) {
		List<Article> articles = new ArrayList<>();
		SecSql sql = new SecSql();

		sql.append("SELECT a.*, m.name AS extra_writer");
		sql.append("FROM article AS a");
		sql.append("LEFT JOIN `member` AS m");
		sql.append("ON a.memberId = m.id");
		sql.append("ORDER BY a.id DESC");
		sql.append("LIMIT ?, ?", limitFrom, limitTake);

		List<Map<String, Object>> articleListMap = DBUtil.selectRows(conn, sql);

		for (Map<String, Object> articleMap : articleListMap) {
			articles.add(new Article(articleMap));
		}
		return articles;
	}

	public List<Article> getArticles(String keyword, int limitFrom, int limitTake) {
		List<Article> articles = new ArrayList<>();
		SecSql sql = new SecSql();

		sql.append("SELECT a.*, m.name AS extra_writer");
		sql.append("FROM article AS a");
		sql.append("LEFT JOIN `member` AS m");
		sql.append("ON a.memberId = m.id");
		sql.append("WHERE a.title LIKE CONCAT('%',?,'%')", keyword);
		sql.append("ORDER BY a.id DESC");
		sql.append("LIMIT ?, ?", limitFrom, limitTake);

		List<Map<String, Object>> articleListMap = DBUtil.selectRows(conn, sql);

		for (Map<String, Object> articleMap : articleListMap) {
			articles.add(new Article(articleMap));
		}
		return articles;
	}

	public void doModify(String title, String body, int id) {
		SecSql sql = new SecSql();

		sql.append("UPDATE article");
		sql.append("SET updateDate = NOW()"); // regDate 입력을 안하면 됨.
		sql.append(", title = ?", title);
		sql.append(", body = ?", body);
		sql.append("WHERE id = ?", id);

		DBUtil.update(conn, sql);
	}

	public Article getArticleById(int id) {
		SecSql sql = new SecSql();

		sql.append("SELECT a.*, m.name AS extra_writer");
		sql.append("FROM article AS a");
		sql.append("LEFT JOIN `member` AS m");
		sql.append("ON a.memberId = m.id");
		sql.append("WHERE a.id = ?", id);

		Map<String, Object> articleMap = DBUtil.selectRow(conn, sql);

		Article article = new Article(articleMap);

		return article;
	}

	public void doDelete(int id) {
		SecSql sql = new SecSql();

		sql.append("DELETE FROM article");
		sql.append("WHERE id = ?", id);

		DBUtil.delete(conn, sql);
	}

	public int getMemberIdById(int id) {
		SecSql sql = new SecSql();

		sql.append("SELECT memberId FROM article");
		sql.append("WHERE id = ?", id);

		return DBUtil.selectRowIntValue(conn, sql);
	}

	public void increaseHit(int id) {
		SecSql sql = new SecSql();

		sql.append("UPDATE article");
		sql.append("SET hit = hit + 1");
		sql.append("WHERE id = ?", id);

		DBUtil.update(conn, sql);
	}

	public int getArticlesCnt(String searchKey) {
		SecSql sql = new SecSql();

		sql.append("SELECT COUNT(*) FROM article");
		if (searchKey != "") {
			sql.append("WHERE title LIKE CONCAT('%',? ,'%')", searchKey);
		}

		return DBUtil.selectRowIntValue(conn, sql);
	}

	public int likeCheck(int id, int logonMemberId) {
		SecSql sql = new SecSql();

		sql.append("SELECT CASE WHEN COUNT(*) != 0 THEN likeType ELSE 0 END");
		sql.append("FROM `like`");
		sql.append("WHERE articleId = ? AND memberId = ?", id, logonMemberId);

		return DBUtil.selectRowIntValue(conn, sql);
	}

	public void insertLike(int id, int likeType, int logonMemberId) {
		SecSql sql = new SecSql();

		sql.append("INSERT INTO `like`");
		sql.append("SET regDate = NOW(), updateDate = NOW()");
		sql.append(", articleId = ?", id);
		sql.append(", memberId = ?", logonMemberId);
		sql.append(", likeType = ?", likeType);

		DBUtil.update(conn, sql);
	}

	public void updateLike(int id, int likeType, int logonMemberId) {
		SecSql sql = new SecSql();

		sql.append("UPDATE `like`");
		sql.append("SET updateDate = NOW()");
		sql.append(", likeType = ?", likeType);
		sql.append("WHERE articleId = ? AND memberId = ?", id, logonMemberId);

		DBUtil.update(conn, sql);
	}

	public void cancelLike(int id, int logonMemberId) {
		SecSql sql = new SecSql();

		sql.append("DELETE FROM `like`");
		sql.append("WHERE articleId = ? AND memberId = ?", id, logonMemberId);

		DBUtil.delete(conn, sql);
	}

	public int getLikeVal(int id, int likeType) {
		SecSql sql = new SecSql();

		sql.append("SELECT COUNT(*) FROM `like`");
		sql.append("WHERE articleId = ? AND likeType = ?2", id, likeType);

		return DBUtil.selectRowIntValue(conn, sql);
	}

	public int doWriteComment(int id, String title, String body, int logonMemberId) {
		SecSql sql = new SecSql();

		sql.append("INSERT INTO `comment`");
		sql.append("SET regDate = NOW()");
		sql.append(", updateDate = NOW()");
		sql.append(", articleid = ?", id);
		sql.append(", memberId = ?", logonMemberId);
		sql.append(", title = ?", title);
		sql.append(", body = ?", body);

		return DBUtil.insert(conn, sql);
	}

	public int getCommentCnt(int commentId) {
		SecSql sql = new SecSql();

		sql.append("SELECT COUNT(*)");
		sql.append("FROM `comment`");
		sql.append("WHERE id = ?", commentId);

		return DBUtil.selectRowIntValue(conn, sql);
	}

	public int getCommentMemberIdById(int commentId) {
		SecSql sql = new SecSql();

		sql.append("SELECT memberId FROM `comment`");
		sql.append("WHERE id = ?", commentId);

		return DBUtil.selectRowIntValue(conn, sql);
	}

	public void doModifyComment(int commentId, String title, String body) {
		SecSql sql = new SecSql();

		sql.append("UPDATE `comment`");
		sql.append("SET updateDate = NOW()");
		sql.append(", title = ?", title);
		sql.append(", `body` = ?", body);
		sql.append("WHERE id = ?", commentId);

		DBUtil.update(conn, sql);
	}

	public boolean isMatchArticleIdtoCommentId(int id, int commentId) {
		SecSql sql = new SecSql();

		sql.append("SELECT COUNT(*) FROM `comment`");
		sql.append("WHERE id = ? AND articleId = ?", commentId, id);

		return !DBUtil.selectRowBooleanValue(conn, sql);// ?
	}

	public List<Comment> getComments(int id) {
		List<Comment> comments = new ArrayList<>();
		SecSql sql = new SecSql();

		
		sql.append("SELECT c.*, m.name AS extra_writer");
		sql.append("FROM `comment` AS c");
		sql.append("LEFT JOIN `member` AS m");
		sql.append("ON c.memberId = m.id");
		sql.append("WHERE c.articleId = ?", id);
		sql.append("ORDER BY c.id DESC");

		List<Map<String, Object>> commentListMap = DBUtil.selectRows(conn, sql);

		for (Map<String, Object> commentMap : commentListMap) {
			comments.add(new Comment(commentMap));
		}
		return comments;
	}

	public void doDeleteComment(int commentId) {
		SecSql sql = new SecSql();

		sql.append("DELETE FROM `comment`");
		sql.append("WHERE id = ?", commentId);

		DBUtil.delete(conn, sql);
	}

	public int getCommentsCnt(int id) {
		SecSql sql = new SecSql();

		sql.append("SELECT COUNT(*) FROM `comment`");
		sql.append("WHERE articleId = ?", id);
		
		return DBUtil.selectRowIntValue(conn, sql);
	}

	public List<Comment> getCommentsByPage(int id, int limitFrom, int limitTake) {
		List<Comment> comments = new ArrayList<>();
		SecSql sql = new SecSql();

		sql.append("SELECT c.*, m.name AS extra_writer");
		sql.append("FROM `comment` AS c");
		sql.append("LEFT JOIN `member` AS m");
		sql.append("ON c.memberId = m.id");
		sql.append("WHERE c.articleId = ?", id);
		sql.append("ORDER BY c.id DESC");
		sql.append("LIMIT ?, ?", limitFrom, limitTake);

		List<Map<String, Object>> commentListMap = DBUtil.selectRows(conn, sql);

		for (Map<String, Object> commentMap : commentListMap) {
			comments.add(new Comment(commentMap));
		}
		return comments;
	}
}
