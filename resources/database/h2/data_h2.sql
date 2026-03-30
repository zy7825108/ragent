-- H2 内存数据库基础数据初始化脚本

-- 插入默认管理员用户
INSERT INTO t_user (id, username, password, role, avatar, create_time, update_time, deleted)
VALUES (2001523723396308993, 'admin', 'admin', 'admin', 'https://static.deepseek.com/user-avatar/G_6cuD8GbD53VwGRwisvCsZ6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 插入示例知识库
INSERT INTO t_knowledge_base (id, name, embedding_model, collection_name, created_by, updated_by, create_time, update_time, deleted)
VALUES (2001523723396308994, '默认知识库', 'qwen-emb-8b', 'default_knowledge_base', '2001523723396308993', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 插入示例意图树节点（领域级）
INSERT INTO t_intent_node (id, kb_id, intent_code, name, level, parent_code, description, examples, collection_name, top_k, mcp_tool_id, kind, prompt_snippet, prompt_template, param_prompt_template, sort_order, enabled, create_by, update_by, create_time, update_time, deleted)
VALUES (2001523723396308995, NULL, 'general', '通用问题', 0, NULL, '处理用户的通用问题和常见疑问', '["如何使用系统", "系统功能介绍", "帮助信息"]', NULL, 5, NULL, 0, NULL, NULL, NULL, 0, 1, '2001523723396308993', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 插入示例意图树节点（类目级）
INSERT INTO t_intent_node (id, kb_id, intent_code, name, level, parent_code, description, examples, collection_name, top_k, mcp_tool_id, kind, prompt_snippet, prompt_template, param_prompt_template, sort_order, enabled, create_by, update_by, create_time, update_time, deleted)
VALUES (2001523723396308996, NULL, 'general:faq', '常见问题', 1, 'general', '解答用户常见问题', '["忘记密码怎么办", "如何修改个人信息", "系统支持哪些文件格式"]', NULL, 3, NULL, 0, NULL, NULL, NULL, 0, 1, '2001523723396308993', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 插入示例意图树节点（话题级）
INSERT INTO t_intent_node (id, kb_id, intent_code, name, level, parent_code, description, examples, collection_name, top_k, mcp_tool_id, kind, prompt_snippet, prompt_template, param_prompt_template, sort_order, enabled, create_by, update_by, create_time, update_time, deleted)
VALUES (2001523723396308997, NULL, 'general:faq:password', '密码相关问题', 2, 'general:faq', '解答与密码相关的问题', '["忘记密码怎么办", "如何修改密码", "密码格式要求"]', NULL, 2, NULL, 0, NULL, NULL, NULL, 0, 1, '2001523723396308993', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 插入示例会话
INSERT INTO t_conversation (id, conversation_id, user_id, title, last_time, create_time, update_time, deleted)
VALUES (2001523723396308998, 'conv_001', '2001523723396308993', '欢迎使用系统', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 插入示例消息
INSERT INTO t_message (id, conversation_id, user_id, role, content, create_time, update_time, deleted)
VALUES (2001523723396308999, 'conv_001', '2001523723396308993', 'user', '你好，我想了解系统的基本功能', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO t_message (id, conversation_id, user_id, role, content, create_time, update_time, deleted)
VALUES (2001523723396309000, 'conv_001', '2001523723396308993', 'assistant', '您好！系统提供以下功能：1) 智能问答 2) 知识库管理 3) 文档上传和解析 4) 深度思考模式', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);