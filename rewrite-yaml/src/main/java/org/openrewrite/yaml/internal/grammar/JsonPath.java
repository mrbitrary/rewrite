/*
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Generated from /Users/jon/Projects/github/openrewrite/rewrite/rewrite-yaml/src/main/antlr/JsonPath.g4 by ANTLR 4.9.2
package org.openrewrite.yaml.internal.grammar;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class JsonPath extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		StringLiteral=1, NumericLiteral=2, AT=3, DOT_DOT=4, DOT=5, ROOT=6, WILDCARD=7, 
		AND=8, EQ=9, NE=10, MATCHES=11, TRUE=12, FALSE=13, NULL=14, LBRACE=15, 
		RBRACE=16, LBRACK=17, RBRACK=18, COLON=19, COMMA=20, LPAREN=21, RPAREN=22, 
		QUESTION=23, Identifier=24, WS=25;
	public static final int
		RULE_jsonpath = 0, RULE_object = 1, RULE_rangeOp = 2, RULE_start = 3, 
		RULE_end = 4, RULE_expression = 5, RULE_litExpression = 6;
	private static String[] makeRuleNames() {
		return new String[] {
			"jsonpath", "object", "rangeOp", "start", "end", "expression", "litExpression"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'@'", "'..'", "'.'", "'$'", "'*'", "'&&'", "'=='", 
			"'!='", "'=~'", "'true'", "'false'", "'null'", "'{'", "'}'", "'['", "']'", 
			"':'", "','", "'('", "')'", "'?'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "StringLiteral", "NumericLiteral", "AT", "DOT_DOT", "DOT", "ROOT", 
			"WILDCARD", "AND", "EQ", "NE", "MATCHES", "TRUE", "FALSE", "NULL", "LBRACE", 
			"RBRACE", "LBRACK", "RBRACK", "COLON", "COMMA", "LPAREN", "RPAREN", "QUESTION", 
			"Identifier", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "JsonPath.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public JsonPath(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class JsonpathContext extends ParserRuleContext {
		public TerminalNode ROOT() { return getToken(JsonPath.ROOT, 0); }
		public List<ObjectContext> object() {
			return getRuleContexts(ObjectContext.class);
		}
		public ObjectContext object(int i) {
			return getRuleContext(ObjectContext.class,i);
		}
		public JsonpathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonpath; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterJsonpath(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitJsonpath(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitJsonpath(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JsonpathContext jsonpath() throws RecognitionException {
		JsonpathContext _localctx = new JsonpathContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_jsonpath);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(15);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ROOT) {
				{
				setState(14);
				match(ROOT);
				}
			}

			setState(18); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(17);
					object();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(20); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ObjectContext extends ParserRuleContext {
		public ObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_object; }
	 
		public ObjectContext() { }
		public void copyFrom(ObjectContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DotOperatorContext extends ObjectContext {
		public TerminalNode DOT() { return getToken(JsonPath.DOT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public DotOperatorContext(ObjectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterDotOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitDotOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitDotOperator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class UnionOperatorContext extends ObjectContext {
		public TerminalNode LBRACK() { return getToken(JsonPath.LBRACK, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode RBRACK() { return getToken(JsonPath.RBRACK, 0); }
		public List<TerminalNode> COMMA() { return getTokens(JsonPath.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(JsonPath.COMMA, i);
		}
		public UnionOperatorContext(ObjectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterUnionOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitUnionOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitUnionOperator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RecursiveDescentContext extends ObjectContext {
		public TerminalNode DOT_DOT() { return getToken(JsonPath.DOT_DOT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public RecursiveDescentContext(ObjectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterRecursiveDescent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitRecursiveDescent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitRecursiveDescent(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BracketOperatorContext extends ObjectContext {
		public TerminalNode LBRACK() { return getToken(JsonPath.LBRACK, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RBRACK() { return getToken(JsonPath.RBRACK, 0); }
		public BracketOperatorContext(ObjectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterBracketOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitBracketOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitBracketOperator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RangeOperatorContext extends ObjectContext {
		public RangeOpContext rangeOp() {
			return getRuleContext(RangeOpContext.class,0);
		}
		public RangeOperatorContext(ObjectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterRangeOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitRangeOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitRangeOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectContext object() throws RecognitionException {
		ObjectContext _localctx = new ObjectContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_object);
		int _la;
		try {
			setState(42);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				_localctx = new BracketOperatorContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(22);
				match(LBRACK);
				setState(23);
				expression(0);
				setState(24);
				match(RBRACK);
				}
				break;
			case 2:
				_localctx = new DotOperatorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(26);
				match(DOT);
				setState(27);
				expression(0);
				}
				break;
			case 3:
				_localctx = new RecursiveDescentContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(28);
				match(DOT_DOT);
				setState(29);
				expression(0);
				}
				break;
			case 4:
				_localctx = new UnionOperatorContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(30);
				match(LBRACK);
				setState(31);
				expression(0);
				setState(36);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(32);
					match(COMMA);
					setState(33);
					expression(0);
					}
					}
					setState(38);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(39);
				match(RBRACK);
				}
				break;
			case 5:
				_localctx = new RangeOperatorContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(41);
				rangeOp();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RangeOpContext extends ParserRuleContext {
		public TerminalNode LBRACK() { return getToken(JsonPath.LBRACK, 0); }
		public StartContext start() {
			return getRuleContext(StartContext.class,0);
		}
		public TerminalNode COLON() { return getToken(JsonPath.COLON, 0); }
		public TerminalNode RBRACK() { return getToken(JsonPath.RBRACK, 0); }
		public EndContext end() {
			return getRuleContext(EndContext.class,0);
		}
		public RangeOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rangeOp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterRangeOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitRangeOp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitRangeOp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RangeOpContext rangeOp() throws RecognitionException {
		RangeOpContext _localctx = new RangeOpContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_rangeOp);
		int _la;
		try {
			setState(58);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(44);
				match(LBRACK);
				setState(45);
				start();
				setState(46);
				match(COLON);
				setState(48);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NumericLiteral) {
					{
					setState(47);
					end();
					}
				}

				setState(50);
				match(RBRACK);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(52);
				match(LBRACK);
				setState(53);
				match(COLON);
				setState(55);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NumericLiteral) {
					{
					setState(54);
					end();
					}
				}

				setState(57);
				match(RBRACK);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StartContext extends ParserRuleContext {
		public TerminalNode NumericLiteral() { return getToken(JsonPath.NumericLiteral, 0); }
		public StartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_start; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterStart(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitStart(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitStart(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StartContext start() throws RecognitionException {
		StartContext _localctx = new StartContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_start);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			match(NumericLiteral);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EndContext extends ParserRuleContext {
		public TerminalNode NumericLiteral() { return getToken(JsonPath.NumericLiteral, 0); }
		public EndContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_end; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterEnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitEnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitEnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EndContext end() throws RecognitionException {
		EndContext _localctx = new EndContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_end);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			match(NumericLiteral);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ParentheticalExpressionContext extends ExpressionContext {
		public TerminalNode LPAREN() { return getToken(JsonPath.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(JsonPath.RPAREN, 0); }
		public ParentheticalExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterParentheticalExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitParentheticalExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitParentheticalExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IdentifierContext extends ExpressionContext {
		public TerminalNode Identifier() { return getToken(JsonPath.Identifier, 0); }
		public IdentifierContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AndExpressionContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode AND() { return getToken(JsonPath.AND, 0); }
		public AndExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterAndExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitAndExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitAndExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PathExpressionContext extends ExpressionContext {
		public JsonpathContext jsonpath() {
			return getRuleContext(JsonpathContext.class,0);
		}
		public PathExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterPathExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitPathExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitPathExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ScopedPathExpressionContext extends ExpressionContext {
		public TerminalNode AT() { return getToken(JsonPath.AT, 0); }
		public List<ObjectContext> object() {
			return getRuleContexts(ObjectContext.class);
		}
		public ObjectContext object(int i) {
			return getRuleContext(ObjectContext.class,i);
		}
		public ScopedPathExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterScopedPathExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitScopedPathExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitScopedPathExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BinaryExpressionContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode EQ() { return getToken(JsonPath.EQ, 0); }
		public TerminalNode NE() { return getToken(JsonPath.NE, 0); }
		public TerminalNode MATCHES() { return getToken(JsonPath.MATCHES, 0); }
		public BinaryExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterBinaryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitBinaryExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitBinaryExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LiteralExpressionContext extends ExpressionContext {
		public LitExpressionContext litExpression() {
			return getRuleContext(LitExpressionContext.class,0);
		}
		public LiteralExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitLiteralExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitLiteralExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WildcardExpressionContext extends ExpressionContext {
		public TerminalNode WILDCARD() { return getToken(JsonPath.WILDCARD, 0); }
		public WildcardExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterWildcardExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitWildcardExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitWildcardExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FilterExpressionContext extends ExpressionContext {
		public TerminalNode QUESTION() { return getToken(JsonPath.QUESTION, 0); }
		public TerminalNode LPAREN() { return getToken(JsonPath.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(JsonPath.RPAREN, 0); }
		public FilterExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterFilterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitFilterExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitFilterExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 10;
		enterRecursionRule(_localctx, 10, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case QUESTION:
				{
				_localctx = new FilterExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(65);
				match(QUESTION);
				setState(66);
				match(LPAREN);
				setState(67);
				expression(0);
				setState(68);
				match(RPAREN);
				}
				break;
			case LPAREN:
				{
				_localctx = new ParentheticalExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(70);
				match(LPAREN);
				setState(71);
				expression(0);
				setState(72);
				match(RPAREN);
				}
				break;
			case StringLiteral:
			case NumericLiteral:
			case TRUE:
			case FALSE:
			case NULL:
				{
				_localctx = new LiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(74);
				litExpression();
				}
				break;
			case Identifier:
				{
				_localctx = new IdentifierContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(75);
				match(Identifier);
				}
				break;
			case DOT_DOT:
			case DOT:
			case ROOT:
			case LBRACK:
				{
				_localctx = new PathExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(76);
				jsonpath();
				}
				break;
			case WILDCARD:
				{
				_localctx = new WildcardExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(77);
				match(WILDCARD);
				}
				break;
			case AT:
				{
				_localctx = new ScopedPathExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(78);
				match(AT);
				setState(80); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(79);
						object();
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(82); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(94);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(92);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
					case 1:
						{
						_localctx = new AndExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(86);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(87);
						match(AND);
						setState(88);
						expression(10);
						}
						break;
					case 2:
						{
						_localctx = new BinaryExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(89);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(90);
						_la = _input.LA(1);
						if ( !((_la & ~0x3f) == 0 && ((1L << _la) & ((1L << EQ) | (1L << NE) | (1L << MATCHES))) != 0) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(91);
						expression(5);
						}
						break;
					}
					} 
				}
				setState(96);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class LitExpressionContext extends ParserRuleContext {
		public TerminalNode StringLiteral() { return getToken(JsonPath.StringLiteral, 0); }
		public TerminalNode NumericLiteral() { return getToken(JsonPath.NumericLiteral, 0); }
		public TerminalNode TRUE() { return getToken(JsonPath.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(JsonPath.FALSE, 0); }
		public TerminalNode NULL() { return getToken(JsonPath.NULL, 0); }
		public LitExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_litExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).enterLitExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JsonPathListener ) ((JsonPathListener)listener).exitLitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JsonPathVisitor ) return ((JsonPathVisitor<? extends T>)visitor).visitLitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LitExpressionContext litExpression() throws RecognitionException {
		LitExpressionContext _localctx = new LitExpressionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_litExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			_la = _input.LA(1);
			if ( !((_la & ~0x3f) == 0 && ((1L << _la) & ((1L << StringLiteral) | (1L << NumericLiteral) | (1L << TRUE) | (1L << FALSE) | (1L << NULL))) != 0) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 5:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 9);
		case 1:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\33f\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\5\2\22\n\2\3\2\6\2\25\n"+
		"\2\r\2\16\2\26\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3%\n"+
		"\3\f\3\16\3(\13\3\3\3\3\3\3\3\5\3-\n\3\3\4\3\4\3\4\3\4\5\4\63\n\4\3\4"+
		"\3\4\3\4\3\4\3\4\5\4:\n\4\3\4\5\4=\n\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\6\7S\n\7\r\7\16\7T\5"+
		"\7W\n\7\3\7\3\7\3\7\3\7\3\7\3\7\7\7_\n\7\f\7\16\7b\13\7\3\b\3\b\3\b\2"+
		"\3\f\t\2\4\6\b\n\f\16\2\4\3\2\13\r\4\2\3\4\16\20\2q\2\21\3\2\2\2\4,\3"+
		"\2\2\2\6<\3\2\2\2\b>\3\2\2\2\n@\3\2\2\2\fV\3\2\2\2\16c\3\2\2\2\20\22\7"+
		"\b\2\2\21\20\3\2\2\2\21\22\3\2\2\2\22\24\3\2\2\2\23\25\5\4\3\2\24\23\3"+
		"\2\2\2\25\26\3\2\2\2\26\24\3\2\2\2\26\27\3\2\2\2\27\3\3\2\2\2\30\31\7"+
		"\23\2\2\31\32\5\f\7\2\32\33\7\24\2\2\33-\3\2\2\2\34\35\7\7\2\2\35-\5\f"+
		"\7\2\36\37\7\6\2\2\37-\5\f\7\2 !\7\23\2\2!&\5\f\7\2\"#\7\26\2\2#%\5\f"+
		"\7\2$\"\3\2\2\2%(\3\2\2\2&$\3\2\2\2&\'\3\2\2\2\')\3\2\2\2(&\3\2\2\2)*"+
		"\7\24\2\2*-\3\2\2\2+-\5\6\4\2,\30\3\2\2\2,\34\3\2\2\2,\36\3\2\2\2, \3"+
		"\2\2\2,+\3\2\2\2-\5\3\2\2\2./\7\23\2\2/\60\5\b\5\2\60\62\7\25\2\2\61\63"+
		"\5\n\6\2\62\61\3\2\2\2\62\63\3\2\2\2\63\64\3\2\2\2\64\65\7\24\2\2\65="+
		"\3\2\2\2\66\67\7\23\2\2\679\7\25\2\28:\5\n\6\298\3\2\2\29:\3\2\2\2:;\3"+
		"\2\2\2;=\7\24\2\2<.\3\2\2\2<\66\3\2\2\2=\7\3\2\2\2>?\7\4\2\2?\t\3\2\2"+
		"\2@A\7\4\2\2A\13\3\2\2\2BC\b\7\1\2CD\7\31\2\2DE\7\27\2\2EF\5\f\7\2FG\7"+
		"\30\2\2GW\3\2\2\2HI\7\27\2\2IJ\5\f\7\2JK\7\30\2\2KW\3\2\2\2LW\5\16\b\2"+
		"MW\7\32\2\2NW\5\2\2\2OW\7\t\2\2PR\7\5\2\2QS\5\4\3\2RQ\3\2\2\2ST\3\2\2"+
		"\2TR\3\2\2\2TU\3\2\2\2UW\3\2\2\2VB\3\2\2\2VH\3\2\2\2VL\3\2\2\2VM\3\2\2"+
		"\2VN\3\2\2\2VO\3\2\2\2VP\3\2\2\2W`\3\2\2\2XY\f\13\2\2YZ\7\n\2\2Z_\5\f"+
		"\7\f[\\\f\6\2\2\\]\t\2\2\2]_\5\f\7\7^X\3\2\2\2^[\3\2\2\2_b\3\2\2\2`^\3"+
		"\2\2\2`a\3\2\2\2a\r\3\2\2\2b`\3\2\2\2cd\t\3\2\2d\17\3\2\2\2\r\21\26&,"+
		"\629<TV^`";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
