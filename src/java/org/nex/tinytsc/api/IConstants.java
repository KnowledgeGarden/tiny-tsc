/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.api;

/**
 * <p>Title: TinyTSC</p>
 * <p>Description: Small "The Scholar's Companion(r)"</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 */

public interface IConstants {
  /**
   * Sentence types
   */
  public static final int ACTORS = 0;
  public static final int IF_ACTORS = 1;
  public static final int IF_NOT_ACTORS = 2;
  public static final int THEN_ACTORS = 3;
  public static final int RELATIONS = 4; // ???? TODO
  public static final int IF_RELATIONS = 5;
  public static final int IF_NOT_RELATIONS = 6;
  public static final int THEN_RELATIONS = 7;
  public static final int STATES = 8;		//???? TODO
  public static final int IF_STATES = 9;
  public static final int IF_NOT_STATES  = 10;
  public static final int THEN_STATES = 11;
  public static final int THEN_CREATE = 12;
  public static final int THEN_CONJECTURE = 13;
  public static final int THEN_SAY = 14;
  public static final int IF_RUN = 15;
  public static final int IF_NOT_RUN = 16;
  public static final int THEN_RUN = 17;

  /**
   * Tree node types
   */
  public static final int CONCEPT = 0;
  /**
   * There will be two kinds of rules:
   * 	ProcessRule
   * 	Heuristic
   */
  public static final int PROCESS_RULE = 1;
  public static final int HEURISTIC_RULE = 2;
  public static final int EPISODE = 3;
  public static final int MODEL = 4;
  public static final int TASK = 5;
  public static final int UNUSED = 6;
  
  /**
   * Known slot types
   */
  public static final String
  	INSTANCE_OF			= "instanceOf",
  	SUB_OF				= "subOf",
  	HAS_SUBS			= "hasSubs",
  	HAS_INSTANCES		= "hasInstances",
  	TRANSITIVE_CLOSURE	= "transitiveClosure",
  	RULES				= "rules",
  	EPISODES			= "episodes",
  	_STATES				= "states",
  	_RELATIONS			= "relations",
  	_ACTORS				= "actors",
  	_IF_ACTORS			= "ifActors",
  	_IF_NOT_ACTORS		= "ifNotActors",
  	_THEN_ACTORS		= "thenActors",
  	_IF_RELATIONS		= "ifRelations",
  	_IF_NOT_RELATIONS	= "ifNotRelations",
  	_THEN_RELATIONS		= "thenRelations",
  	_IF_STATES			= "ifStates",
  	_IF_NOT_STATES		= "ifNotStates",
  	_THEN_STATES		= "thenStates",
  	_THEN_CREATE		= "thenCreate",
  	_THEN_CONJECTURE	= "thenConjectures",
  	_THEN_SAY			= "thenSay",
  	//TODO the following are not in ProcessRule yet
  	_IF_RUN				= "ifRun",
  	_IF_NOT_RUN			= "ifNotRun",
  	_THEN_RUN			= "thenRun",
  	_NEXT_EPIODE		= "nextEpisode",
  	_PREVIOUS_EPISODE	= "previousEpisode";

  /**
   * Classes and objects
   */
  public static final String
  	_CONCEPT			= "concept",
  	_TASK				= "task",
  	_RULE				= "rule",
  	_MODEL				= "model",
  	_EPISODE			= "episode",
  	_SENTENCE			= "sentence",
  	_SLOT				= "slot",
  	_PREDICATE			= "predicate",
  	_SUBJECT			= "subject",
  	_MECHANISM			= "mechanism",
  	_NODE				= "node",
  	_TYPE				= "type",
  	_OBJECT				= "object",
  	_OBJECT_B			= "objectB",
  	_EXPERIMENT			= "experiment",
  	_VALUE				= "value",
  	_PRIORITY			= "priority",
  	_COMMENT			= "comment",
  	_NAME				= "name",
  	_TRUTH				= "truth",
  	_MY_MECHANISM		= "myMechanism",
  	_DATABASE			= "database";
  
  /**
   * QP Task types
   * There can be many more task types for discovery, etc
   */
  public static final String 
  	FILLIN_NEXT_EPISODE 	= "fillinNextEp",
  	PUBLISH_EPISODE 		= "publishEp",
  	FIND_EPISODE 			= "findEp";

}