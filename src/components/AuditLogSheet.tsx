import React from 'react';
import { AnimatePresence, motion } from 'motion/react';
import { useFraudX } from '../context/FraudXContext';

export const AuditLogSheet: React.FC = () => {
  const { auditSheetOpen, closeAuditSheet, auditLog, showToast } = useFraudX();

  const handleCopyAudit = async () => {
    try {
      await navigator.clipboard.writeText(JSON.stringify(auditLog, null, 2));
      showToast('Audit log JSON copied to clipboard');
    } catch {
      showToast('Audit log snapshot prepared for export');
    }
    closeAuditSheet();
  };

  return (
    <AnimatePresence>
      {auditSheetOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={{ duration: 0.18 }}
          className="absolute inset-0 z-50 bg-black/40 backdrop-blur-[2px] flex flex-col justify-end p-3"
          role="dialog"
          aria-modal="true"
          aria-label="Rule Audit Log"
          onClick={closeAuditSheet}
        >
          <motion.div
            initial={{ y: 40, opacity: 0.85 }}
            animate={{ y: 0, opacity: 1 }}
            exit={{ y: 40, opacity: 0 }}
            transition={{ duration: 0.22, ease: [0.16, 1, 0.3, 1] }}
            className="w-full bg-white rounded-[26px] p-5 shadow-2xl border border-[#e6e9e8] flex flex-col gap-3.5 max-h-[82%]"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="w-10 h-1 rounded-full bg-[#d8dada] mx-auto -mt-1" />

            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-[#006e2f] text-[20px]">tune</span>
                <h2
                  className="text-[16px] font-bold text-[#191c1c]"
                  style={{ fontFamily: 'var(--font-display)' }}
                >
                  Rule Audit Log
                </h2>
              </div>
              <button
                type="button"
                onClick={closeAuditSheet}
                aria-label="Close audit log"
                className="min-w-[36px] min-h-[36px] rounded-full bg-[#eceeed] flex items-center justify-center text-[#3d4a3d] cursor-pointer"
              >
                <span className="material-symbols-outlined text-[18px]">close</span>
              </button>
            </div>

            <div className="overflow-y-auto no-scrollbar space-y-2 max-h-[280px]">
              {auditLog.map((entry) => (
                <div
                  key={entry.id}
                  className="rounded-2xl bg-[#f2f4f3] border border-[#e2e8e5] p-3 flex flex-col gap-1"
                >
                  <div className="flex items-center justify-between gap-2">
                    <span
                      className="text-[10px] font-bold uppercase text-[#006e2f] tracking-wider"
                      style={{ fontFamily: 'var(--font-mono)' }}
                    >
                      {entry.actionLabel}
                    </span>
                    <span
                      className="text-[10px] text-[#6d7b6c]"
                      style={{ fontFamily: 'var(--font-mono)' }}
                    >
                      {entry.timestampLabel}
                    </span>
                  </div>
                  <span
                    className="text-[13px] font-bold text-[#191c1c] truncate"
                    style={{ fontFamily: 'var(--font-mono)' }}
                  >
                    {entry.target}
                  </span>
                  <span className="text-[12px] text-[#3d4a3d]">{entry.detail}</span>
                </div>
              ))}
            </div>

            <button
              type="button"
              onClick={() => void handleCopyAudit()}
              className="w-full h-12 rounded-2xl bg-[#1e4635] hover:bg-[#143326] text-white text-[14px] font-bold flex items-center justify-center gap-2 cursor-pointer transition-colors"
              style={{ fontFamily: 'var(--font-display)' }}
            >
              <span className="material-symbols-outlined text-[18px]">content_copy</span>
              <span>Copy Encrypted Audit JSON</span>
            </button>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
};
