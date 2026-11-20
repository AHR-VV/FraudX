import React, { useEffect, useMemo, useState } from 'react';
import { FraudXTopBar } from '../components/FraudXTopBar';
import { useFraudX } from '../context/FraudXContext';
import type { InterceptionChannel, InterceptionStatus } from '../types/fraudx';

export const InterceptionLogScreen: React.FC = () => {
  const { navigate, interceptions, selectedInterceptionId, escalateInterceptionToRule } =
    useFraudX();

  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<'all' | InterceptionStatus>('all');
  const [channelFilter, setChannelFilter] = useState<'all' | InterceptionChannel>('all');
  const [expandedId, setExpandedId] = useState<string | null>(selectedInterceptionId);

  useEffect(() => {
    setExpandedId(selectedInterceptionId);
  }, [selectedInterceptionId]);

  const blockedCount = useMemo(
    () => interceptions.filter((i) => i.status === 'blocked').length,
    [interceptions]
  );
  const clearedCount = useMemo(
    () => interceptions.filter((i) => i.status === 'cleared').length,
    [interceptions]
  );

  const filteredEvents = useMemo(() => {
    const q = searchQuery.trim().toLowerCase();
    return interceptions.filter((ev) => {
      if (statusFilter !== 'all' && ev.status !== statusFilter) return false;
      if (channelFilter !== 'all' && ev.channel !== channelFilter) return false;
      if (!q) return true;
      return (
        ev.target.toLowerCase().includes(q) ||
        ev.summary.toLowerCase().includes(q) ||
        ev.sourceDetail.toLowerCase().includes(q)
      );
    });
  }, [interceptions, searchQuery, statusFilter, channelFilter]);

  return (
    <div className="flex flex-col h-full bg-[#f8faf9]">
      <FraudXTopBar
        subtitle="Interception Log"
        isRootDashboard={false}
        subtitleAccentGreen={true}
        onLeadingClick={() => navigate('dashboard')}
      />

      <div className="flex-1 min-h-0 overflow-y-auto no-scrollbar">
        <div className="px-5 pt-4 pb-10 flex flex-col gap-4">
        {/* Summary Metrics Strip */}
        <div className="grid grid-cols-3 gap-2.5">
          <div className="rounded-[20px] bg-white border border-[#e2e8e5] p-3 flex flex-col gap-0.5 shadow-2xs">
            <span
              className="text-[22px] font-bold text-[#133324] tabular-nums leading-tight"
              style={{ fontFamily: 'var(--font-mono)' }}
            >
              {interceptions.length}
            </span>
            <span className="text-[11px] font-semibold text-[#6d7b6c]">Total</span>
          </div>
          <div className="rounded-[20px] bg-white border border-[#e2e8e5] p-3 flex flex-col gap-0.5 shadow-2xs">
            <span
              className="text-[22px] font-bold text-[#ba1a1a] tabular-nums leading-tight"
              style={{ fontFamily: 'var(--font-mono)' }}
            >
              {blockedCount}
            </span>
            <span className="text-[11px] font-semibold text-[#6d7b6c]">Blocked</span>
          </div>
          <div className="rounded-[20px] bg-white border border-[#e2e8e5] p-3 flex flex-col gap-0.5 shadow-2xs">
            <span
              className="text-[22px] font-bold text-[#006e2f] tabular-nums leading-tight"
              style={{ fontFamily: 'var(--font-mono)' }}
            >
              {clearedCount}
            </span>
            <span className="text-[11px] font-semibold text-[#6d7b6c]">Cleared</span>
          </div>
        </div>

        {/* Search */}
        <div className="relative flex items-center">
          <span className="material-symbols-outlined text-[20px] text-[#6d7b6c] absolute left-3.5 pointer-events-none">
            search
          </span>
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search intercepted domains, numbers..."
            className="w-full h-11 bg-white border border-[#bccbb9]/60 text-[#191c1c] placeholder:text-[#6d7b6c] pl-10 pr-9 rounded-full text-[13px] focus:outline-none focus:border-[#006e2f]"
          />
          {searchQuery && (
            <button
              type="button"
              onClick={() => setSearchQuery('')}
              aria-label="Clear log search"
              className="absolute right-2.5 p-1 text-[#3d4a3d] cursor-pointer"
            >
              <span className="material-symbols-outlined text-[16px]">close</span>
            </button>
          )}
        </div>

        {/* Status Segmented Controls */}
        <div className="grid grid-cols-3 gap-2">
          {(
            [
              { id: 'all', label: 'All Events' },
              { id: 'blocked', label: 'Blocked' },
              { id: 'cleared', label: 'Cleared' },
            ] as const
          ).map((tab) => {
            const active = statusFilter === tab.id;
            return (
              <button
                key={tab.id}
                type="button"
                onClick={() => setStatusFilter(tab.id)}
                className={`h-10 rounded-full text-[12px] font-bold transition-all border cursor-pointer ${
                  active
                    ? 'bg-[#1e4635] text-white border-[#1e4635] shadow-xs'
                    : 'bg-white text-[#3d4a3d] border-[#e2e8e5] hover:border-[#bccbb9]'
                }`}
                style={{ fontFamily: 'var(--font-display)' }}
              >
                {tab.label}
              </button>
            );
          })}
        </div>

        {/* Channel Filter Pills */}
        <div className="flex items-center gap-1.5 overflow-x-auto no-scrollbar">
          {(
            [
              { id: 'all', label: 'All Channels' },
              { id: 'website', label: 'Website' },
              { id: 'sms', label: 'SMS' },
              { id: 'phone', label: 'Phone Call' },
            ] as const
          ).map((ch) => {
            const active = channelFilter === ch.id;
            return (
              <button
                key={ch.id}
                type="button"
                onClick={() => setChannelFilter(ch.id)}
                className={`px-3.5 py-1.5 rounded-full text-[11px] whitespace-nowrap shrink-0 border transition-colors cursor-pointer ${
                  active
                    ? 'bg-[#d8f8b5] text-[#1c4a07] font-bold border-[#c7ebd4]'
                    : 'bg-white text-[#3d4a3d] font-semibold border-[#bccbb9]/60'
                }`}
              >
                {ch.label}
              </button>
            );
          })}
        </div>

        {/* Expandable Interception Cards */}
        {filteredEvents.length === 0 ? (
          <div className="rounded-[24px] bg-white border border-[#e2e8e5] p-8 text-center flex flex-col items-center gap-2">
            <span className="material-symbols-outlined text-[28px] text-[#6d7b6c]">
              history_toggle_off
            </span>
            <span
              className="text-[15px] font-bold text-[#191c1c]"
              style={{ fontFamily: 'var(--font-display)' }}
            >
              No matching interceptions
            </span>
            <button
              type="button"
              onClick={() => {
                setSearchQuery('');
                setStatusFilter('all');
                setChannelFilter('all');
              }}
              className="mt-2 px-4 py-1.5 rounded-full bg-[#d8f8b5] text-[#1c4a07] text-[12px] font-bold cursor-pointer"
            >
              Reset Filters
            </button>
          </div>
        ) : (
          <div className="flex flex-col gap-3">
            {filteredEvents.map((ev) => {
              const isBlocked = ev.status === 'blocked';
              const isExpanded = expandedId === ev.id;

              return (
                <div
                  key={ev.id}
                  className={`rounded-[24px] bg-white border transition-colors overflow-hidden shadow-2xs ${
                    isExpanded ? 'border-[#006e2f]/45' : 'border-[#e2e8e5]'
                  }`}
                >
                  <button
                    type="button"
                    onClick={() => setExpandedId(isExpanded ? null : ev.id)}
                    className="w-full p-4 flex items-center justify-between gap-2.5 text-left cursor-pointer"
                  >
                    <div className="flex items-center gap-3 min-w-0">
                      <div
                        className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 ${
                          isBlocked ? 'bg-[#fee2e2] text-[#dc2626]' : 'bg-[#c7ebd4] text-[#006e2f]'
                        }`}
                      >
                        <span className="material-symbols-outlined text-[20px]">
                          {isBlocked ? 'gpp_bad' : 'security_update_good'}
                        </span>
                      </div>
                      <div className="flex flex-col min-w-0">
                        <div className="flex items-center gap-1.5">
                          <span
                            className="text-[13px] font-bold text-[#191c1c] truncate"
                            style={{ fontFamily: 'var(--font-mono)' }}
                          >
                            {ev.target}
                          </span>
                          <span
                            className={`px-1.5 py-0.5 rounded-md text-[9.5px] font-bold leading-none shrink-0 ${
                              isBlocked
                                ? 'bg-[#ba1a1a] text-white'
                                : 'bg-[#c7ebd4] text-[#2e4d3d]'
                            }`}
                          >
                            {isBlocked ? 'BLOCKED' : 'CLEARED'}
                          </span>
                        </div>
                        <span className="text-[12px] text-[#6d7b6c] truncate mt-0.5">
                          {ev.channel === 'website'
                            ? 'Website'
                            : ev.channel === 'phone'
                              ? 'Phone Call'
                              : 'SMS'}{' '}
                          • {ev.summary}
                        </span>
                      </div>
                    </div>

                    <div className="flex flex-col items-end shrink-0">
                      <span className="text-[10.5px] font-semibold text-[#6d7b6c]">
                        {ev.relativeTime}
                      </span>
                      <span className="material-symbols-outlined text-[18px] text-[#6d7b6c]">
                        {isExpanded ? 'expand_less' : 'expand_more'}
                      </span>
                    </div>
                  </button>

                  {isExpanded && (
                    <div className="px-4 pb-4 pt-3 bg-[#f2f4f3]/65 border-t border-[#e2e8e5] flex flex-col gap-3">
                      <div className="flex items-start justify-between gap-2">
                        <span className="text-[12px] text-[#3d4a3d] leading-snug">
                          {ev.sourceDetail}
                        </span>
                        <span
                          className={`text-[11px] font-bold shrink-0 tabular-nums ${
                            isBlocked ? 'text-[#ba1a1a]' : 'text-[#006e2f]'
                          }`}
                          style={{ fontFamily: 'var(--font-mono)' }}
                        >
                          Risk {ev.riskScore}/100
                        </span>
                      </div>

                      <ul className="space-y-1">
                        {ev.heuristicSignals.map((sig) => (
                          <li
                            key={sig}
                            className="text-[12px] text-[#3d4a3d] flex items-start gap-1.5"
                          >
                            <span aria-hidden="true" className="text-[#6d7b6c]">
                              •
                            </span>
                            <span>{sig}</span>
                          </li>
                        ))}
                      </ul>

                      <div className="grid grid-cols-2 gap-2 pt-1">
                        <button
                          type="button"
                          onClick={() => void escalateInterceptionToRule(ev, 'blocked')}
                          className="h-9 rounded-xl bg-[#ba1a1a] hover:bg-[#991b1b] text-white text-[12px] font-bold cursor-pointer transition-colors"
                        >
                          Add to Blocklist
                        </button>
                        <button
                          type="button"
                          onClick={() => void escalateInterceptionToRule(ev, 'allowed')}
                          className="h-9 rounded-xl bg-white hover:bg-[#eceeed] border border-[#d8dada] text-[#191c1c] text-[12px] font-semibold cursor-pointer transition-colors"
                        >
                          Trust in Allowlist
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
        </div>
      </div>
    </div>
  );
};
