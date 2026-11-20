import React from 'react';
import { FraudXSwitch } from '../components/FraudXSwitch';
import { FraudXTopBar } from '../components/FraudXTopBar';
import { useFraudX } from '../context/FraudXContext';
import type { DbUpdateFrequency } from '../types/fraudx';

export const SettingsScreen: React.FC = () => {
  const {
    navigate,
    protection,
    armedModuleCount,
    allowedCount,
    blockedCount,
    isSyncingDb,
    toggleModule,
    updateDbFrequency,
    syncThreatDatabase,
    setInstantPushAlerts,
    setVoiceAssistance,
  } = useFraudX();

  const bannerConfig =
    armedModuleCount === 3
      ? {
          title: 'Full Perimeter Defense',
          subtitle: '3 of 3 Modules Armed',
          iconBg: 'bg-[#c7ebd4] text-[#006e2f]',
          statusText: 'text-[#006e2f]',
          dotBg: 'bg-[#22c55e]',
          glowBg: 'bg-[#22c55e]/12',
          icon: 'verified_user',
        }
      : armedModuleCount === 2
        ? {
            title: 'Partial Perimeter Defense',
            subtitle: '2 of 3 Modules Armed',
            iconBg: 'bg-amber-100 text-amber-700',
            statusText: 'text-amber-700',
            dotBg: 'bg-amber-500',
            glowBg: 'bg-amber-500/12',
            icon: 'shield',
          }
        : armedModuleCount === 1
          ? {
              title: 'Partial Perimeter Defense',
              subtitle: '1 of 3 Modules Armed',
              iconBg: 'bg-[#ffdad6] text-[#ba1a1a]',
              statusText: 'text-[#ba1a1a]',
              dotBg: 'bg-[#ba1a1a]',
              glowBg: 'bg-[#ba1a1a]/12',
              icon: 'gpp_bad',
            }
          : {
              title: 'Perimeter Defense Disabled',
              subtitle: '0 of 3 Modules Armed (Vulnerable)',
              iconBg: 'bg-[#ffdad6] text-[#ba1a1a]',
              statusText: 'text-[#ba1a1a]',
              dotBg: 'bg-[#ba1a1a]',
              glowBg: 'bg-[#ba1a1a]/15',
              icon: 'security_update_warning',
            };

  return (
    <div className="flex flex-col h-full bg-[#f8faf9]">
      <FraudXTopBar
        subtitle="Settings"
        isRootDashboard={false}
        subtitleAccentGreen={true}
        onLeadingClick={() => navigate('dashboard')}
      />

      <div className="flex-1 min-h-0 overflow-y-auto no-scrollbar">
        <div className="px-5 pt-4 pb-10 flex flex-col gap-6">
          {/* Dynamic Perimeter Status Overview Banner */}
          <div className="relative shrink-0 overflow-hidden bg-white border border-[#e2e8e5] rounded-[24px] p-4 shadow-[0_2px_10px_rgba(19,51,36,0.04)]">
            <div className="flex items-center gap-3.5 relative z-10">
              <div
                className={`relative flex items-center justify-center w-11 h-11 rounded-full shrink-0 transition-colors duration-300 ${bannerConfig.iconBg}`}
              >
                <span className="material-symbols-outlined material-symbols-filled text-[24px]">
                  {bannerConfig.icon}
                </span>
                <span className="absolute -top-0.5 -right-0.5 flex h-3 w-3">
                  {armedModuleCount > 0 && (
                    <span
                      className={`animate-ping absolute inline-flex h-full w-full rounded-full opacity-75 ${bannerConfig.dotBg}`}
                    />
                  )}
                  <span
                    className={`relative inline-flex rounded-full h-3 w-3 ring-2 ring-white ${bannerConfig.dotBg}`}
                  />
                </span>
              </div>

              <div className="flex flex-col min-w-0">
                <span
                  className="text-[16px] leading-[22px] font-bold text-[#133324] truncate"
                  style={{ fontFamily: 'var(--font-display)' }}
                >
                  {bannerConfig.title}
                </span>
                <span
                  className={`text-[12.5px] leading-[18px] font-semibold tracking-wide transition-colors ${bannerConfig.statusText}`}
                >
                  {bannerConfig.subtitle}
                </span>
              </div>
            </div>

            <div
              className={`absolute -right-6 -bottom-8 w-36 h-36 rounded-full pointer-events-none blur-2xl transition-colors duration-300 ${bannerConfig.glowBg}`}
            />
          </div>

        {/* Primary Intercept Modules */}
        <section className="flex flex-col gap-3">
          <div className="flex items-center justify-between px-1">
            <h2 className="text-[13px] font-bold uppercase tracking-[0.06em] text-[#133324]">
              Primary Intercept Modules
            </h2>
            <span
              className="text-[10.5px] font-semibold text-[#6d7b6c]"
              style={{ fontFamily: 'var(--font-mono)' }}
            >
              {protection.policyStamp}
            </span>
          </div>

          {/* Website Protection */}
          <div className="bg-white border border-[#e2e8e5] rounded-[24px] p-4 shadow-2xs flex flex-col gap-2.5">
            <div className="flex items-start justify-between gap-3">
              <div className="flex items-start gap-3 min-w-0">
                <div className="w-10 h-10 rounded-full bg-[#c7ebd4] flex items-center justify-center text-[#006e2f] shrink-0 mt-0.5">
                  <span className="material-symbols-outlined text-[22px]">public</span>
                </div>
                <div className="flex flex-col min-w-0">
                  <span
                    className="text-[16px] font-bold text-[#133324]"
                    style={{ fontFamily: 'var(--font-display)' }}
                  >
                    Website Protection
                  </span>
                  <span className="text-[11px] font-semibold text-[#006e2f]">
                    Live Zero-Day Link Intercept
                  </span>
                </div>
              </div>
              <FraudXSwitch
                checked={protection.modules.website}
                onChange={() => void toggleModule('website')}
                ariaLabel="Toggle Website Protection"
              />
            </div>

            <p className="text-[13.5px] leading-[20px] text-[#3d4a3d]">
              Blocks phishing domains, malicious scripts, zero-day malware links, and deceptive
              redirects in Safari &amp; Chrome via real-time SSL stream validation.
            </p>

            <div className="flex items-center gap-1.5 text-[12px] font-medium pt-0.5">
              <span
                className={`material-symbols-outlined text-[15px] ${
                  protection.modules.website ? 'text-[#006e2f]' : 'text-[#6d7b6c]'
                }`}
              >
                bolt
              </span>
              <span
                className={`tabular-nums ${
                  protection.modules.website ? 'text-[#006e2f]' : 'text-[#6d7b6c]'
                }`}
              >
                {protection.modules.website
                  ? `${protection.websiteBlockedThisWeek.toLocaleString()} blocked this week`
                  : 'Module paused'}
              </span>
            </div>
          </div>

          {/* SMS Protection */}
          <div className="bg-white border border-[#e2e8e5] rounded-[24px] p-4 shadow-2xs flex flex-col gap-2.5">
            <div className="flex items-start justify-between gap-3">
              <div className="flex items-start gap-3 min-w-0">
                <div className="w-10 h-10 rounded-full bg-[#c7ebd4] flex items-center justify-center text-[#006e2f] shrink-0 mt-0.5">
                  <span className="material-symbols-outlined text-[22px]">sms</span>
                </div>
                <div className="flex flex-col min-w-0">
                  <span
                    className="text-[16px] font-bold text-[#133324]"
                    style={{ fontFamily: 'var(--font-display)' }}
                  >
                    SMS Protection
                  </span>
                  <span className="text-[11px] font-semibold text-[#006e2f]">
                    Natural Language Smishing Filter
                  </span>
                </div>
              </div>
              <FraudXSwitch
                checked={protection.modules.sms}
                onChange={() => void toggleModule('sms')}
                ariaLabel="Toggle SMS Protection"
              />
            </div>

            <p className="text-[13.5px] leading-[20px] text-[#3d4a3d]">
              Filters smishing fraud messages, bank impersonation texts, and unverified sender links
              before notifications reach your lock screen.
            </p>

            <div className="flex items-center gap-1.5 text-[12px] font-medium pt-0.5">
              <span
                className={`material-symbols-outlined text-[15px] ${
                  protection.modules.sms ? 'text-[#006e2f]' : 'text-[#6d7b6c]'
                }`}
              >
                shield_with_heart
              </span>
              <span className={protection.modules.sms ? 'text-[#006e2f]' : 'text-[#6d7b6c]'}>
                {protection.modules.sms ? 'Zero inbox compromise' : 'Module paused'}
              </span>
            </div>
          </div>

          {/* Call Protection */}
          <div className="bg-white border border-[#e2e8e5] rounded-[24px] p-4 shadow-2xs flex flex-col gap-2.5">
            <div className="flex items-start justify-between gap-3">
              <div className="flex items-start gap-3 min-w-0">
                <div className="w-10 h-10 rounded-full bg-[#c7ebd4] flex items-center justify-center text-[#006e2f] shrink-0 mt-0.5">
                  <span className="material-symbols-outlined text-[22px]">phone_in_talk</span>
                </div>
                <div className="flex flex-col min-w-0">
                  <span
                    className="text-[16px] font-bold text-[#133324]"
                    style={{ fontFamily: 'var(--font-display)' }}
                  >
                    Call Protection
                  </span>
                  <span className="text-[11px] font-semibold text-[#006e2f]">
                    Carrier-Grade Caller ID Screening
                  </span>
                </div>
              </div>
              <FraudXSwitch
                checked={protection.modules.call}
                onChange={() => void toggleModule('call')}
                ariaLabel="Toggle Call Protection"
              />
            </div>

            <p className="text-[13.5px] leading-[20px] text-[#3d4a3d]">
              Real-time caller ID scam screening, robocall silent blocking, and instant spoofed
              geographic footprint detection.
            </p>

            <div className="flex items-center gap-1.5 text-[12px] font-medium pt-0.5">
              <span
                className={`material-symbols-outlined text-[15px] ${
                  protection.modules.call ? 'text-[#006e2f]' : 'text-[#6d7b6c]'
                }`}
              >
                check_circle
              </span>
              <span className={protection.modules.call ? 'text-[#006e2f]' : 'text-[#6d7b6c]'}>
                {protection.modules.call ? 'Telephony honeynet active' : 'Module paused'}
              </span>
            </div>
          </div>
        </section>

        {/* Secondary Preferences */}
        <section className="flex flex-col gap-2.5">
          <div className="flex items-center justify-between px-1">
            <h2 className="text-[13px] font-bold uppercase tracking-[0.06em] text-[#133324]">
              Secondary Preferences
            </h2>
            <span className="text-[10.5px] font-semibold text-[#6d7b6c] tracking-wide">
              AUTO PILOT
            </span>
          </div>

          <div className="bg-white border border-[#e2e8e5] rounded-[24px] overflow-hidden shadow-2xs divide-y divide-[#e2e8e5]">
            {/* Threat Database Updates */}
            <div className="p-4 flex items-center justify-between gap-2.5 hover:bg-[#f8faf9] transition-colors">
              <button
                type="button"
                onClick={() => void syncThreatDatabase()}
                className="flex items-center gap-3 min-w-0 text-left cursor-pointer group"
                title="Tap to sync threat definitions now"
              >
                <div className="w-9 h-9 rounded-full bg-[#f2f4f3] border border-[#e2e8e5] flex items-center justify-center text-[#133324] shrink-0 group-hover:border-[#22c55e] transition-colors">
                  <span
                    className={`material-symbols-outlined text-[20px] ${
                      isSyncingDb ? 'animate-spin text-[#006e2f]' : ''
                    }`}
                  >
                    sync
                  </span>
                </div>
                <div className="flex flex-col min-w-0">
                  <span
                    className="text-[15px] font-semibold text-[#133324] truncate"
                    style={{ fontFamily: 'var(--font-display)' }}
                  >
                    Threat Database Updates
                  </span>
                  <span className="text-[12px] text-[#3d4a3d] truncate">
                    Last updated: {protection.lastDbSyncLabel}
                  </span>
                </div>
              </button>

              <div className="relative shrink-0 flex items-center bg-[#f2f4f3] hover:bg-[#eceeed] border border-[#e2e8e5] rounded-full px-3 py-1.5">
                <select
                  aria-label="Threat database update frequency"
                  value={protection.dbUpdateFrequency}
                  onChange={(e) => void updateDbFrequency(e.target.value as DbUpdateFrequency)}
                  className="appearance-none bg-transparent text-[#133324] text-[10.5px] uppercase font-bold tracking-wide cursor-pointer focus:outline-none pr-4 z-10"
                >
                  <option value="DAILY">DAILY</option>
                  <option value="WEEKLY">WEEKLY</option>
                  <option value="MONTHLY">MONTHLY</option>
                </select>
                <span className="material-symbols-outlined text-[15px] text-[#3d4a3d] pointer-events-none absolute right-2">
                  expand_more
                </span>
              </div>
            </div>

            {/* Instant Push Alerts */}
            <div className="p-4 flex items-center justify-between gap-2.5 hover:bg-[#f8faf9] transition-colors">
              <div className="flex items-center gap-3 min-w-0">
                <div className="w-9 h-9 rounded-full bg-[#f2f4f3] border border-[#e2e8e5] flex items-center justify-center text-[#133324] shrink-0">
                  <span className="material-symbols-outlined text-[20px]">
                    notifications_active
                  </span>
                </div>
                <div className="flex flex-col min-w-0">
                  <span
                    className="text-[15px] font-semibold text-[#133324] truncate"
                    style={{ fontFamily: 'var(--font-display)' }}
                  >
                    Instant Push Alerts
                  </span>
                  <span className="text-[12px] text-[#3d4a3d]">
                    Immediate priority breach notifications
                  </span>
                </div>
              </div>
              <FraudXSwitch
                checked={protection.instantPushAlerts}
                onChange={(next) => void setInstantPushAlerts(next)}
                ariaLabel="Toggle Instant Push Alerts"
              />
            </div>

            {/* Voice Assistance */}
            <div className="p-4 flex items-center justify-between gap-2.5 hover:bg-[#f8faf9] transition-colors">
              <div className="flex items-center gap-3 min-w-0">
                <div className="w-9 h-9 rounded-full bg-[#f2f4f3] border border-[#e2e8e5] flex items-center justify-center text-[#133324] shrink-0">
                  <span className="material-symbols-outlined text-[20px]">mic</span>
                </div>
                <div className="flex flex-col min-w-0">
                  <span
                    className="text-[15px] font-semibold text-[#133324] truncate"
                    style={{ fontFamily: 'var(--font-display)' }}
                  >
                    Voice Assistance
                  </span>
                  <span className="text-[12px] text-[#3d4a3d]">
                    Real-time audio alerts and guidance for incoming threat warnings
                  </span>
                </div>
              </div>
              <FraudXSwitch
                checked={protection.voiceAssistance}
                onChange={(next) => void setVoiceAssistance(next)}
                ariaLabel="Toggle Voice Assistance"
              />
            </div>

            {/* Allow / Block Manually */}
            <button
              type="button"
              onClick={() => navigate('allow_block')}
              className="w-full p-4 flex items-center justify-between gap-2.5 hover:bg-[#f2f4f3]/70 transition-colors text-left cursor-pointer"
            >
              <div className="flex items-center gap-3 min-w-0">
                <div className="w-9 h-9 rounded-full bg-[#f2f4f3] border border-[#e2e8e5] flex items-center justify-center text-[#133324] shrink-0">
                  <span className="material-symbols-outlined text-[20px]">rule_folder</span>
                </div>
                <div className="flex flex-col min-w-0">
                  <span
                    className="text-[15px] font-semibold text-[#133324] truncate"
                    style={{ fontFamily: 'var(--font-display)' }}
                  >
                    Allow/Block Manually
                  </span>
                  <span className="text-[12px] text-[#3d4a3d] tabular-nums">
                    {allowedCount} allowed rules, {blockedCount} blocked rules
                  </span>
                </div>
              </div>
              <div className="flex items-center gap-0.5 text-[#006e2f] shrink-0">
                <span className="text-[11px] font-bold uppercase tracking-wide">Manage</span>
                <span className="material-symbols-outlined text-[18px]">chevron_right</span>
              </div>
            </button>
          </div>
        </section>

        {/* Encrypted Telemetry Footnote */}
        <div className="flex items-center justify-between px-1 text-[#3d4a3d]/85">
          <div className="flex items-center gap-1.5 text-[11px] font-semibold">
            <span className="material-symbols-outlined text-[15px] text-[#006e2f]">security</span>
            <span>All telemetry encrypted locally</span>
          </div>
          <span
            className="text-[10.5px] font-semibold text-[#6d7b6c]"
            style={{ fontFamily: 'var(--font-mono)' }}
          >
            {protection.buildStamp}
          </span>
        </div>
        </div>
      </div>
    </div>
  );
};
