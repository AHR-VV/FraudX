import React, { useMemo, useState } from 'react';
import { FraudXTopBar } from '../components/FraudXTopBar';
import { useFraudX } from '../context/FraudXContext';
import type { ManualRule, RuleAction, RuleCategory } from '../types/fraudx';

export const AllowBlockScreen: React.FC = () => {
  const {
    navigate,
    rules,
    allowedCount,
    blockedCount,
    addManualRule,
    flipRuleAction,
    deleteRule,
    openAuditSheet,
    showToast,
  } = useFraudX();

  const [activeTab, setActiveTab] = useState<RuleAction>('allowed');
  const [newTarget, setNewTarget] = useState('');
  const [addMode, setAddMode] = useState<RuleAction>('allowed');
  const [addCategory, setAddCategory] = useState<RuleCategory>('website');

  const [searchQuery, setSearchQuery] = useState('');
  const [categoryFilter, setCategoryFilter] = useState<'all' | RuleCategory>('all');
  const [openMenuRuleId, setOpenMenuRuleId] = useState<string | null>(null);

  const filteredRules = useMemo(() => {
    const q = searchQuery.trim().toLowerCase();
    return rules.filter((rule) => {
      if (rule.action !== activeTab) return false;
      if (categoryFilter !== 'all' && rule.category !== categoryFilter) return false;
      if (!q) return true;
      return (
        rule.target.toLowerCase().includes(q) ||
        rule.description.toLowerCase().includes(q) ||
        rule.tag.toLowerCase().includes(q)
      );
    });
  }, [rules, activeTab, categoryFilter, searchQuery]);

  const handleQuickAdd = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTarget.trim()) return;
    void addManualRule(newTarget, addMode, addCategory);
    setActiveTab(addMode);
    setNewTarget('');
  };

  const handleCopyTarget = async (target: string) => {
    setOpenMenuRuleId(null);
    try {
      await navigator.clipboard.writeText(target);
      showToast(`Copied "${target}"`);
    } catch {
      showToast(`Selected "${target}"`);
    }
  };

  const getRuleIcon = (rule: ManualRule) => {
    if (rule.action === 'allowed') {
      return rule.category === 'website' ? 'language' : rule.category === 'phone' ? 'call' : 'sms';
    }
    return rule.category === 'website'
      ? 'public_off'
      : rule.category === 'phone'
        ? 'phone_disabled'
        : 'phonelink_erase';
  };

  return (
    <div className="flex flex-col h-full bg-[#f8faf9]" onClick={() => setOpenMenuRuleId(null)}>
      <FraudXTopBar
        subtitle="Settings"
        isRootDashboard={false}
        subtitleAccentGreen={true}
        onLeadingClick={() => navigate('settings')}
      />

      <div className="flex-1 min-h-0 overflow-y-auto no-scrollbar">
        <div className="px-5 pt-3 pb-10 flex flex-col gap-4">
        {/* Allowed vs Blocked Master Segmented Tabs */}
        <div className="p-1 rounded-full bg-[#f2f4f3] border border-[#e6e9e8] grid grid-cols-2 gap-1.5 shadow-2xs">
          <button
            type="button"
            onClick={() => setActiveTab('allowed')}
            className={`min-h-[42px] flex items-center justify-center gap-2 py-2 px-3 rounded-full text-[14px] transition-all cursor-pointer ${
              activeTab === 'allowed'
                ? 'bg-[#d8f8b5] text-[#1c4a07] font-bold shadow-xs'
                : 'text-[#3d4a3d] hover:text-[#191c1c] font-semibold'
            }`}
            style={{ fontFamily: 'var(--font-display)' }}
          >
            <span className="material-symbols-outlined text-[18px]">verified_user</span>
            <span>Allowed</span>
            <span className="px-2 py-0.5 rounded-full bg-white text-[#006e2f] text-[10px] font-bold tabular-nums shadow-2xs">
              {allowedCount}
            </span>
          </button>

          <button
            type="button"
            onClick={() => setActiveTab('blocked')}
            className={`min-h-[42px] flex items-center justify-center gap-2 py-2 px-3 rounded-full text-[14px] transition-all cursor-pointer ${
              activeTab === 'blocked'
                ? 'bg-[#ffdad6] text-[#93000a] font-bold shadow-xs'
                : 'text-[#3d4a3d] hover:text-[#191c1c] font-semibold'
            }`}
            style={{ fontFamily: 'var(--font-display)' }}
          >
            <span className="material-symbols-outlined text-[18px]">gpp_bad</span>
            <span>Blocked</span>
            <span
              className={`px-2 py-0.5 rounded-full text-[10px] font-bold tabular-nums ${
                activeTab === 'blocked'
                  ? 'bg-white text-[#ba1a1a]'
                  : 'bg-[#e1e3e2] text-[#ba1a1a]'
              }`}
            >
              {blockedCount}
            </span>
          </button>
        </div>

        {/* Inject New Rule (L3-Filter) Card */}
        <form
          onSubmit={handleQuickAdd}
          className="rounded-[24px] bg-white border border-[#bccbb9]/50 p-4 flex flex-col gap-3 shadow-2xs"
        >
          <div className="flex items-center justify-between gap-2">
            <div className="flex items-center gap-1.5 text-[#006e2f]">
              <span className="material-symbols-outlined text-[20px]">add_moderator</span>
              <span
                className="text-[16px] font-bold text-[#191c1c]"
                style={{ fontFamily: 'var(--font-display)' }}
              >
                Inject New Rule
              </span>
            </div>
            <span
              className="text-[10px] font-bold uppercase tracking-wider px-2.5 py-0.5 rounded-full bg-[#f2f4f3] border border-[#e6e9e8] text-[#3d4a3d]"
              style={{ fontFamily: 'var(--font-mono)' }}
            >
              L3-FILTER
            </span>
          </div>

          <div className="flex items-center gap-2">
            <div className="relative flex-1 min-w-0">
              <span className="material-symbols-outlined text-[18px] text-[#6d7b6c] absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none">
                terminal
              </span>
              <input
                type="text"
                value={newTarget}
                onChange={(e) => setNewTarget(e.target.value)}
                placeholder="e.g. domain.com, +1800..."
                className="w-full h-11 pl-9 pr-3 bg-[#f2f4f3]/80 border border-[#bccbb9]/70 rounded-full text-[13.5px] text-[#191c1c] placeholder:text-[#6d7b6c]/80 focus:outline-none focus:border-[#006e2f] focus:bg-white transition-colors"
                style={{ fontFamily: 'var(--font-mono)' }}
              />
            </div>

            <button
              type="submit"
              disabled={!newTarget.trim()}
              className="h-11 px-4 rounded-full bg-[#22c55e] hover:bg-[#006e2f] disabled:opacity-50 text-white text-[14px] font-bold flex items-center justify-center gap-1 active:scale-95 transition-all shrink-0 cursor-pointer shadow-xs"
              style={{ fontFamily: 'var(--font-display)' }}
            >
              <span className="material-symbols-outlined text-[18px]">add</span>
              <span>Add</span>
            </button>
          </div>

          <div className="flex items-center justify-between gap-2 pt-0.5">
            <div className="flex items-center gap-2 min-w-0">
              <button
                type="button"
                onClick={() => setAddMode((prev) => (prev === 'allowed' ? 'blocked' : 'allowed'))}
                className={`px-3 py-1 rounded-full text-[10.5px] font-bold uppercase flex items-center gap-1.5 border cursor-pointer transition-colors shrink-0 ${
                  addMode === 'allowed'
                    ? 'bg-[#c7ebd4] text-[#2e4d3d] border-[#abcfb9]/50'
                    : 'bg-[#ffdad6] text-[#93000a] border-[#ffdad6]'
                }`}
              >
                <span
                  className={`w-2 h-2 rounded-full ${
                    addMode === 'allowed' ? 'bg-[#006e2f]' : 'bg-[#ba1a1a]'
                  }`}
                />
                <span>{addMode === 'allowed' ? 'ALLOW' : 'BLOCK'}</span>
              </button>

              <div className="relative inline-flex items-center">
                <select
                  aria-label="New rule category"
                  value={addCategory}
                  onChange={(e) => setAddCategory(e.target.value as RuleCategory)}
                  className="appearance-none bg-[#eceeed] text-[#191c1c] text-[10.5px] font-bold uppercase pl-3 pr-7 py-1 rounded-full border border-[#bccbb9]/60 focus:outline-none cursor-pointer"
                >
                  <option value="website">WEB / URL</option>
                  <option value="phone">PHONE / CALLER ID</option>
                  <option value="sms">SMS KEYWORD</option>
                </select>
                <span className="material-symbols-outlined text-[14px] text-[#3d4a3d] absolute right-2 pointer-events-none">
                  expand_more
                </span>
              </div>
            </div>

            <span
              className="text-[10px] font-semibold text-[#3d4a3d] shrink-0"
              style={{ fontFamily: 'var(--font-mono)' }}
            >
              AUTO-ACTIVE
            </span>
          </div>
        </form>

        {/* Search & Category Filter Carousel */}
        <div className="flex flex-col gap-2.5">
          <div className="relative flex items-center w-full">
            <span className="material-symbols-outlined text-[20px] text-[#6d7b6c] absolute left-3.5 pointer-events-none">
              search
            </span>
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search targets, tags, keywords..."
              className="w-full h-11 bg-white border border-[#bccbb9]/60 text-[#191c1c] placeholder:text-[#6d7b6c] pl-10 pr-10 rounded-full text-[13px] focus:outline-none focus:border-[#006e2f] shadow-2xs transition-colors"
            />
            {searchQuery && (
              <button
                type="button"
                onClick={() => setSearchQuery('')}
                aria-label="Clear search"
                className="absolute right-2.5 p-1.5 text-[#3d4a3d] hover:text-[#191c1c] cursor-pointer"
              >
                <span className="material-symbols-outlined text-[16px]">close</span>
              </button>
            )}
          </div>

          <div className="flex items-center gap-1.5 overflow-x-auto no-scrollbar py-0.5">
            {(
              [
                { id: 'all', label: 'All Records', icon: null },
                { id: 'website', label: 'Websites / URLs', icon: 'language' },
                { id: 'phone', label: 'Phone Numbers', icon: 'call' },
                { id: 'sms', label: 'SMS Keywords', icon: 'sms' },
              ] as const
            ).map((chip) => {
              const active = categoryFilter === chip.id;
              return (
                <button
                  key={chip.id}
                  type="button"
                  onClick={() => setCategoryFilter(chip.id)}
                  className={`px-3.5 py-1.5 rounded-full text-[11px] whitespace-nowrap shrink-0 flex items-center gap-1 border transition-all cursor-pointer ${
                    active
                      ? 'bg-[#d8f8b5] text-[#1c4a07] font-bold border-[#c7ebd4] shadow-2xs'
                      : 'bg-white text-[#3d4a3d] hover:text-[#191c1c] font-semibold border-[#bccbb9]/60'
                  }`}
                  style={{ fontFamily: 'var(--font-display)' }}
                >
                  {chip.icon && (
                    <span className="material-symbols-outlined text-[14px]">{chip.icon}</span>
                  )}
                  <span>{chip.label}</span>
                </button>
              );
            })}
          </div>
        </div>

        {/* Active List Header */}
        <div className="flex items-center justify-between gap-2 px-0.5 pt-1">
          <div className="flex items-center gap-2 min-w-0">
            <h2
              className="text-[16px] font-bold text-[#191c1c] tracking-tight truncate"
              style={{ fontFamily: 'var(--font-display)' }}
            >
              {activeTab === 'allowed' ? 'Whitelist Perimeter' : 'Blacklist Perimeter'}
            </h2>
            <span className="px-2.5 py-0.5 rounded-full bg-[#e6e9e8] text-[#3d4a3d] text-[10.5px] font-medium whitespace-nowrap tabular-nums">
              Displaying {filteredRules.length} {filteredRules.length === 1 ? 'item' : 'items'}
            </span>
          </div>

          <button
            type="button"
            onClick={openAuditSheet}
            className="flex items-center gap-1 text-[#3d4a3d] hover:text-[#006e2f] transition-colors shrink-0 py-1 px-1 cursor-pointer"
          >
            <span className="material-symbols-outlined text-[16px]">tune</span>
            <span className="text-[10.5px] font-bold uppercase tracking-wide">Audit Log</span>
          </button>
        </div>

        {/* Rules List or Empty State */}
        {filteredRules.length === 0 ? (
          <div className="flex flex-col items-center justify-center text-center p-8 bg-white border border-[#bccbb9]/50 rounded-[24px]">
            <div className="w-12 h-12 rounded-full bg-[#eceeed] flex items-center justify-center text-[#3d4a3d] mb-2">
              <span className="material-symbols-outlined text-[26px]">search_off</span>
            </div>
            <span
              className="text-[16px] font-bold text-[#191c1c]"
              style={{ fontFamily: 'var(--font-display)' }}
            >
              No Rules Found
            </span>
            <p className="text-[12.5px] text-[#3d4a3d] max-w-[240px] mt-1">
              No custom overrides match your active search terms or category filter.
            </p>
            <button
              type="button"
              onClick={() => {
                setSearchQuery('');
                setCategoryFilter('all');
              }}
              className="mt-4 px-4 py-2 bg-[#d8f8b5] hover:bg-[#c7ebd4] text-[#1c4a07] text-[12px] font-bold rounded-full transition-colors cursor-pointer"
            >
              Clear Filters
            </button>
          </div>
        ) : (
          <div className="flex flex-col gap-3">
            {filteredRules.map((rule) => {
              const isAllowed = rule.action === 'allowed';
              const isMenuOpen = openMenuRuleId === rule.id;

              return (
                <div
                  key={rule.id}
                  className={`relative rounded-[24px] bg-white border shadow-2xs overflow-visible ${
                    isAllowed ? 'border-[#bccbb9]/45' : 'border-[#ffdad6]'
                  }`}
                >
                  <div className="p-4 flex items-start justify-between gap-2">
                    <div className="flex items-start gap-3 min-w-0">
                      <div
                        className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 border ${
                          isAllowed
                            ? 'bg-[#c7ebd4]/70 border-[#abcfb9]/50 text-[#006e2f]'
                            : 'bg-[#ffdad6]/75 border-[#ffdad6] text-[#ba1a1a]'
                        }`}
                      >
                        <span className="material-symbols-outlined text-[20px]">
                          {getRuleIcon(rule)}
                        </span>
                      </div>

                      <div className="flex flex-col min-w-0">
                        <div className="flex items-center gap-2 flex-wrap">
                          <span
                            className="text-[15px] font-bold text-[#191c1c] truncate max-w-[190px]"
                            style={{ fontFamily: 'var(--font-mono)' }}
                          >
                            {rule.target}
                          </span>
                          <span
                            className={`px-2 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider ${
                              isAllowed
                                ? 'bg-[#d8f8b5] text-[#1c4a07]'
                                : 'bg-[#ffdad6] text-[#93000a]'
                            }`}
                          >
                            {rule.badgeLabel}
                          </span>
                        </div>
                        <span
                          className={`text-[12.5px] mt-1 leading-snug ${
                            isAllowed ? 'text-[#3d4a3d]' : 'text-[#ba1a1a]'
                          }`}
                        >
                          {rule.description}
                        </span>
                      </div>
                    </div>

                    {/* 3-dot Overflow Action Menu */}
                    <div
                      className="relative shrink-0"
                      onClick={(e) => {
                        e.stopPropagation();
                      }}
                    >
                      <button
                        type="button"
                        aria-label={`More actions for ${rule.target}`}
                        onClick={() => setOpenMenuRuleId(isMenuOpen ? null : rule.id)}
                        className="w-9 h-9 rounded-full flex items-center justify-center text-[#3d4a3d] hover:bg-[#f2f4f3] cursor-pointer transition-colors"
                      >
                        <span className="material-symbols-outlined text-[18px]">more_vert</span>
                      </button>

                      {isMenuOpen && (
                        <div className="absolute right-0 top-9 z-30 w-44 rounded-2xl bg-white border border-[#e2e8e5] shadow-xl py-1.5 text-[12.5px]">
                          <button
                            type="button"
                            onClick={() => {
                              setOpenMenuRuleId(null);
                              void flipRuleAction(rule.id);
                            }}
                            className="w-full px-3.5 py-2 text-left text-[#191c1c] hover:bg-[#f2f4f3] font-medium cursor-pointer"
                          >
                            {isAllowed ? 'Move to Blocked' : 'Move to Allowed'}
                          </button>
                          <button
                            type="button"
                            onClick={() => void handleCopyTarget(rule.target)}
                            className="w-full px-3.5 py-2 text-left text-[#191c1c] hover:bg-[#f2f4f3] font-medium cursor-pointer"
                          >
                            Copy Target
                          </button>
                          <button
                            type="button"
                            onClick={() => {
                              setOpenMenuRuleId(null);
                              deleteRule(rule.id);
                            }}
                            className="w-full px-3.5 py-2 text-left text-[#ba1a1a] hover:bg-[#fff1f2] font-semibold cursor-pointer"
                          >
                            Delete Rule
                          </button>
                        </div>
                      )}
                    </div>
                  </div>

                  {/* Card Footer */}
                  <div
                    className={`flex items-center justify-between gap-2 px-4 py-2.5 border-t rounded-b-[24px] ${
                      isAllowed
                        ? 'border-[#e6e9e8] bg-[#f2f4f3]/60'
                        : 'border-[#ffdad6]/60 bg-[#ffdad6]/15'
                    }`}
                  >
                    <div
                      className="flex items-center gap-2 min-w-0 text-[10px] uppercase truncate"
                      style={{ fontFamily: 'var(--font-mono)' }}
                    >
                      <span
                        className={`font-medium truncate ${
                          !isAllowed && rule.tag.startsWith('TRIGGER')
                            ? 'text-[#ba1a1a]'
                            : 'text-[#6d7b6c]'
                        }`}
                      >
                        {rule.tag}
                      </span>
                      <span className="text-[#bccbb9]">•</span>
                      <span className="text-[#6d7b6c] shrink-0">{rule.createdAtLabel}</span>
                    </div>

                    <button
                      type="button"
                      onClick={() => deleteRule(rule.id)}
                      className="flex items-center gap-1 text-[#3d4a3d] hover:text-[#ba1a1a] transition-colors shrink-0 py-0.5 cursor-pointer"
                    >
                      <span className="material-symbols-outlined text-[16px]">delete</span>
                      <span className="text-[10.5px] font-bold uppercase">Delete</span>
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {/* Notice Card */}
        <div className="mt-1 p-4 rounded-[24px] bg-[#f2f4f3] border border-[#bccbb9]/55 flex items-start gap-2.5">
          <span className="material-symbols-outlined text-[20px] text-[#3b6925] shrink-0 mt-0.5">
            policy
          </span>
          <div className="flex flex-col">
            <span
              className="text-[10.5px] text-[#3b6925] uppercase font-bold tracking-wider"
              style={{ fontFamily: 'var(--font-mono)' }}
            >
              Notice
            </span>
            <p className="text-[12.5px] text-[#3d4a3d] mt-0.5 leading-snug">
              Manual allow exceptions supersede all other rules
            </p>
          </div>
        </div>
        </div>
      </div>
    </div>
  );
};
