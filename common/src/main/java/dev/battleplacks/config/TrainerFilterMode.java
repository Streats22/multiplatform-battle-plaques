package dev.battleplacks.config;

/** Which NPC trainers get the cards. Matching is done on the trainer's display name against the configured patterns. */
public enum TrainerFilterMode { ALL, ONLY_MATCHING, EXCEPT_MATCHING }
